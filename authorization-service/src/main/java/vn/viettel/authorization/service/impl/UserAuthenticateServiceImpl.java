package vn.viettel.authorization.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.authorization.repository.*;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.*;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.authorization.*;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserAuthenticateServiceImpl extends BaseServiceImpl<User, UserRepository> implements UserAuthenticateService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    RolePermissionMapRepository rolePermissionMapRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    OrgAccessRepository orgAccessRepository;

    @Autowired
    FunctionAccessRepository functionAccessRepository;

    @Autowired
    FormRepository formRepository;

    @Autowired
    ControlRepository controlRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Autowired
    ShopRepository shopRepository;

    private User user;

    @Override
    public Response<LoginResponse> preLogin(LoginRequest loginInfo, String captchaCode) {
        Response<LoginResponse> response = checkLoginValid(loginInfo);

        if (Boolean.FALSE.equals(response.getSuccess()))
            return response;

        user = repository.findByUsername(loginInfo.getUsername());
        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        if (user.getWrongTime() > user.getMaxWrongTime()) {
            if (loginInfo.getCaptchaCode() == null)
                return response.withError(ResponseMessage.ENTER_CAPTCHA_TO_LOGIN);
            if (!loginInfo.getCaptchaCode().equals(captchaCode))
                return response.withError(ResponseMessage.WRONG_CAPTCHA);
        }
        List<RoleDTO> roleList = getUserRoles(user.getId());
        if (roleList.isEmpty())
            return response.withError(ResponseMessage.USER_HAVE_NO_ROLE);
        List<Permission> permissionList = permissionRepository.findByListRoleId(getUserRoleIds(user.getId()));
        if (permissionList.isEmpty()) return response.withError(ResponseMessage.NO_PERMISSION_ASSIGNED);

        List<ShopDTO> shops = new ArrayList<>();
        for (RoleDTO roleDTO : roleList) {
            shops.addAll(getShopByRole(roleDTO.getId()));
            roleDTO.setShops(getShopByRole(roleDTO.getId()));
        }
        if (shops.isEmpty())
            return response.withError(ResponseMessage.NO_PRIVILEGE_ON_ANY_SHOP);

        if (roleList.size() == 1 && shops.size() == 1) {
            if (getUserPermission(roleList.get(0).getId()).isEmpty())
                return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);
            resData.setUsedShop(shops.get(0));
            resData.setUsedRole(roleList.get(0));
            resData.setPermissions(getUserPermission(roleList.get(0).getId()));
            response.setToken(createToken(roleList.get(0).getRoleName(), shops.get(0).getShopId(), roleList.get(0).getId()));
        }
        resData.setRoles(roleList);
        response.setData(resData);

        user.setWrongTime(0);
        repository.save(user);
        return response.withData(resData);
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginInfo) {
        Response<LoginResponse> response = checkLoginValid(loginInfo);

        if (Boolean.FALSE.equals(response.getSuccess()))
            return response;

        user = repository.findByUsername(loginInfo.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        if (!checkUserMatchRole(user.getId(), loginInfo.getRoleId()))
            return response.withError(ResponseMessage.USER_ROLE_NOT_MATCH);
        Role role = new Role();
        if (roleRepository.findById(loginInfo.getRoleId()).isPresent())
            role = roleRepository.findById(loginInfo.getRoleId()).get();

        if (getUserPermission(role.getId()).isEmpty() || !checkShopByRole(loginInfo.getRoleId(), loginInfo.getShopId()))
            return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);

        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        Shop shop = new Shop();
        if (shopRepository.findById(loginInfo.getShopId()).isPresent())
            shop = shopRepository.findById(loginInfo.getShopId()).get();

        resData.setUsedShop(new ShopDTO(loginInfo.getShopId(), shop.getShopName()));
        resData.setPermissions(getUserPermission(loginInfo.getRoleId()));

        resData.setRoles(null);
        resData.setUsedRole(modelMapper.map(role, RoleDTO.class));

        response.setToken(createToken(role.getRoleName(), shop.getId(), role.getId()));
        response.setData(resData);
        return response;
    }

    @Override
    public Response<String> changePassword(ChangePasswordRequest request) {
        Response<String> response = new Response<>();

        user = repository.findByUsername(request.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            return response.withError(ResponseMessage.USER_OLD_PASSWORD_NOT_CORRECT);

        if (request.getNewPassword().length() < 8 || request.getNewPassword().length() > 20)
            return new Response<String>().withError(ResponseMessage.INVALID_PASSWORD_LENGTH);

        if (request.getOldPassword().equals(request.getNewPassword()))
            return response.withError(ResponseMessage.DUPLICATE_PASSWORD);

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            return response.withError(ResponseMessage.CONFIRM_PASSWORD_NOT_CORRECT);

        if (user.getPasswordConfig() != null && user.getPasswordConfig() == 1 &&
                checkPassword(request.getNewPassword()).getData() == null)
            return checkPassword(request.getNewPassword());

        String securePassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(securePassword);
        try {
            repository.save(user);
        } catch (Exception e) {
            return response.withError(ResponseMessage.CHANGE_PASSWORD_FAIL);
        }
        return response.withData(ResponseMessage.SUCCESSFUL.toString());
    }

    public String createToken(String role, Long shopId, Long roleId) {
        return jwtTokenCreate.createToken(ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).withShopId(shopId).withRoleId(roleId).get());
    }

    public boolean checkShopByRole(Long roleId, Long shopId) {
        return getShopByRole(roleId).stream().anyMatch(shop -> shop.getShopId().equals(shopId));
    }

    public boolean checkUserMatchRole(Long userId, Long roleId) {
        return getUserRoles(userId).stream().anyMatch(role -> role.getId().equals(roleId));
    }

    public Response<LoginResponse> checkLoginValid(LoginRequest loginInfo) {
        Response<LoginResponse> response = new Response<>();
        response.setSuccess(false);

        user = repository.findByUsername(loginInfo.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        int wrongTime = user.getWrongTime();
        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            wrongTime++;
            user.setWrongTime(wrongTime);
            repository.save(user);
            return response.withError(ResponseMessage.INCORRECT_PASSWORD);
        }
        if (user.getStatus() == 0)
            return response.withError(ResponseMessage.USER_IS_NOT_ACTIVE);

        if (loginInfo.getShopId() != null) {
            if (Boolean.FALSE.equals(shopRepository.findById(loginInfo.getShopId()).isPresent()))
                return response.withError(ResponseMessage.SHOP_NOT_FOUND);
            if (shopRepository.findById(loginInfo.getShopId()).get().getStatus() != 1)
                return response.withError(ResponseMessage.SHOP_IS_NOT_ACTIVE);
        }
        response.setSuccess(true);
        return response;
    }

    public List<RoleDTO> getUserRoles(Long userId) {
        List<RoleDTO> roles = new ArrayList<>();
        userRoleRepository.findByUserId(userId).
                forEach(e -> {
                    if (roleRepository.findById(e.getRoleId()).isPresent())
                        roles.add(modelMapper.map(roleRepository.findById(e.getRoleId()).get(), RoleDTO.class));
                });
        return roles;
    }

    public List<Long> getUserRoleIds(Long userId) {
        List<Long> roles = new ArrayList<>();
        userRoleRepository.findByUserId(userId).
                forEach(e -> roles.add(e.getRoleId()));
        return roles;
    }

    public List<ShopDTO> getUserManageShops(Long roleId) {
        List<ShopDTO> result = new ArrayList<>();
        List<BigDecimal> listShopId = orgAccessRepository.finShopIdByRoleId(roleId);

        for (BigDecimal shopId : listShopId) {
            if (shopRepository.findById(shopId.longValue()).isPresent()) {
                Shop shop = shopRepository.findById(shopId.longValue()).get();
                ShopDTO shopDTO = new ShopDTO(shop.getId(), shop.getShopName());
                result.add(shopDTO);
            }
        }
        return result;
    }

    @Override
    public List<PermissionDTO> getUserPermission(Long roleId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<FunctionAccess> functionAccessList = functionAccessRepository.findByRoleId(roleId);

        for (FunctionAccess funcAccess : functionAccessList) {
            Permission permission = new Permission();
            if (permissionRepository.findById(funcAccess.getPermissionId()).isPresent())
                permission = permissionRepository.findById(funcAccess.getPermissionId()).get();
            if (permission.getIsFullPrivilege() == 1)
                result.addAll(getPermissionWhenFullPrivilege(permission.getId()));

            if (formRepository.findById(funcAccess.getFormId()).isPresent())
                setUserPermission(result, formRepository.findById(funcAccess.getFormId()).get(),
                        controlRepository.findByFormId(funcAccess.getFormId()), funcAccess.getShowStatus());
        }
        return result;
    }

    public List<PermissionDTO> getPermissionWhenFullPrivilege(Long permissionId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionId(permissionId);

        for (FunctionAccess funcAccess : functionAccess) {
            if (formRepository.findById(funcAccess.getFormId()).isPresent())
                setUserPermission(result, formRepository.findById(funcAccess.getFormId()).get(),
                        controlRepository.findByFormId(funcAccess.getFormId()), funcAccess.getShowStatus());
        }
        return result;
    }

    public void setUserPermission(List<PermissionDTO> result, Form form, List<Control> controls, int showStatus) {
        PermissionDTO permissionDTO = modelMapper.map(form, PermissionDTO.class);
        List<ControlDTO> listControl = controls.stream().map(ctrl -> modelMapper.map(ctrl, ControlDTO.class)).collect(Collectors.toList());

        for (ControlDTO control : listControl)
            control.setShowStatus(ShowStatus.getValueOf(showStatus));
        permissionDTO.setControls(listControl);

        if (!checkPermissionContain(result, form))
            result.add(permissionDTO);
    }

    public boolean checkPermissionContain(List<PermissionDTO> list, Form form) {
        return list.stream().anyMatch(perm -> perm.getFormCode().equalsIgnoreCase(form.getFormCode()));
    }

    @Override
    public User getUserById(long id) {
        return repository.findById(id).isPresent() ? repository.findById(id).get() : null;
    }

    @Override
    public List<ShopDTO> getShopByRole(Long roleId) {
        return getUserManageShops(roleId);
    }

    public Response<String> checkPassword(String password) {
        boolean containNum = false;
        boolean containUpperCase = false;
        boolean containLowerCase = false;

        for (int i = 0; i < password.length(); i++) {
            char letter = password.charAt(i);
            if (Character.isDigit(letter))
                containNum = true;
            else if (Character.isUpperCase(letter))
                containUpperCase = true;
            else if (Character.isLowerCase(letter))
                containLowerCase = true;
        }
        boolean containSpecialCharacter = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return containNum && containUpperCase && containLowerCase && containSpecialCharacter ? new Response<String>().withData("OK") :
                new Response<String>().withError(ResponseMessage.INVALID_PASSWORD_FORMAT);
    }
}
