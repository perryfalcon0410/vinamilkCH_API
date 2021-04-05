package vn.viettel.authorization.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.authorization.repository.*;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.UserAuthenticateService;
import vn.viettel.authorization.service.dto.*;
import vn.viettel.authorization.service.feign.ShopClient;
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
    ShopClient shopClient;

    private User user;

    @Override
    public Response<LoginResponse> preLogin(LoginRequest loginInfo, String captchaCode) {
        Response<LoginResponse> response = checkLoginValid(loginInfo);

        if (response.getSuccess() == false)
            return response.withError(ResponseMessage.LOGIN_FAILED);

        user = repository.findByUsername(loginInfo.getUsername());
        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        if (user.getWrongTime() > user.getMaxWrongTime()) {
            if (loginInfo.getCaptchaCode() == null)
                return response.withError(ResponseMessage.ENTER_CAPTCHA_TO_LOGIN);
            if (!loginInfo.getCaptchaCode().equals(captchaCode))
                return response.withError(ResponseMessage.WRONG_CAPTCHA);
        }
        List<RoleDTO> roleList = getUserRoles(user.getId());
        if (roleList.size() == 0)
            return response.withError(ResponseMessage.USER_HAVE_NO_ROLE);
        List<Permission> permissionList = permissionRepository.findByListRoleId(getUserRoleIds(user.getId()));
        if (permissionList.size() == 0) return response.withError(ResponseMessage.NO_PERMISSION_ASSIGNED);

        List<ShopDTO> shops = new ArrayList<>();
        for (int i = 0; i < roleList.size(); i++) {
            shops.addAll(getShopByRole(roleList.get(i).getId()));
            roleList.get(i).setShops(getShopByRole(roleList.get(i).getId()));
        }
        if (shops.size() == 0)
            return response.withError(ResponseMessage.NO_PRIVILEGE_ON_ANY_SHOP);

        if (roleList.size() == 1 && shops.size() == 1) {
            if (getUserPermission(roleList.get(0).getId()).size() == 0)
                return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);
            resData.setUsedShop(shops.get(0));
            resData.setUsedRole(roleList.get(0));
            resData.setPermissions(getUserPermission(roleList.get(0).getId()));
            response.setToken(createToken(roleList.get(0).getRoleName()));
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

        if (response.getSuccess() == false)
            return response;

        User user = repository.findByUsername(loginInfo.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        if (!checkUserMatchRole(user.getId(), loginInfo.getRoleId()))
            return response.withError(ResponseMessage.USER_ROLE_NOT_MATCH);
        Role role = roleRepository.findById(loginInfo.getRoleId()).get();

        if (getUserPermission(role.getId()).size() == 0 || !checkShopByRole(loginInfo.getRoleId(), loginInfo.getShopId()))
            return response.withError(ResponseMessage.NO_FUNCTIONAL_PERMISSION);

        LoginResponse resData = modelMapper.map(user, LoginResponse.class);
        Shop shop = shopClient.getShopById(loginInfo.getShopId()).getData();

        resData.setUsedShop(new ShopDTO(loginInfo.getShopId(), shop.getShopName()));
        resData.setPermissions(getUserPermission(loginInfo.getRoleId()));

        resData.setRoles(null);
        resData.setUsedRole(modelMapper.map(role, RoleDTO.class));

        response.setToken(createToken(role.getRoleName()));
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

        if (request.getOldPassword().equals(request.getNewPassword()))
            return response.withError(ResponseMessage.DUPLICATE_PASSWORD);

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            return response.withError(ResponseMessage.CONFIRM_PASSWORD_NOT_CORRECT);

        if (user.getPasswordConfig() == 1 && checkPassword(request.getNewPassword()).getData() == null)
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

    public String createToken(String role) {
        return jwtTokenCreate.createToken(ClaimsTokenBuilder.build(role).withUserId(user.getId()).get());
    }
    public boolean checkShopByRole(Long roleId, Long shopId) {
        return getShopByRole(roleId).stream().filter(shop -> shop.getShopId().equals(shopId)).findFirst().isPresent();
    }
    public boolean checkUserMatchRole(Long userId, Long roleId) {
        return getUserRoles(userId).stream().filter(role -> role.getId().equals(roleId)).findFirst().isPresent();
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
            if (shopClient.getShopById(loginInfo.getShopId()).getSuccess() == false)
                return response.withError(ResponseMessage.SHOP_NOT_FOUND);
            if (shopClient.getShopById(loginInfo.getShopId()).getData().getStatus() != 1)
                return response.withError(ResponseMessage.SHOP_IS_NOT_ACTIVE);
        }
        response.setSuccess(true);
        return response;
    }

    public List<RoleDTO> getUserRoles(Long userId) {
        List<RoleDTO> roles = new ArrayList<>();
        userRoleRepository.findByUserId(userId).stream().
                forEach(e -> roles.add(modelMapper.map(roleRepository.findById(e.getRoleId()).get(), RoleDTO.class)));
        return roles;
    }
    public List<Long> getUserRoleIds(Long userId) {
        List<Long> roles = new ArrayList<>();
        userRoleRepository.findByUserId(userId).stream().
                forEach(e -> roles.add(e.getRoleId()));
        return roles;
    }

    public List<ShopDTO> getUserManageShops(Long roleId) {
        List<ShopDTO> result = new ArrayList<>();
        List<BigDecimal> listShopId = orgAccessRepository.finShopIdByRoleId(roleId);

        for (BigDecimal shopId : listShopId) {
            Shop shop = shopClient.getShopById(shopId.longValue()).getData();
            if (shop != null) {
                ShopDTO shopDTO = new ShopDTO(shop.getId(), shop.getShopName());
                result.add(shopDTO);
            }
        }
        return result;
    }

    public List<PermissionDTO> getUserPermission(Long roleId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<FunctionAccess> functionAccessList = functionAccessRepository.findByRoleId(roleId);

        for (FunctionAccess funcAccess : functionAccessList) {
            Permission permission = permissionRepository.findById(funcAccess.getPermissionId()).get();
            if (permission.getIsFullPrivilege() == 1)
                result.addAll(getPermissionWhenFullPrivilege(permission.getId()));

            setUserPermission(result, formRepository.findById(funcAccess.getFormId()).get(),
                    controlRepository.findByFormId(funcAccess.getFormId()), funcAccess.getShowStatus());
        }
        return result;
    }
    public List<PermissionDTO> getPermissionWhenFullPrivilege(Long permissionId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionId(permissionId);

        for (FunctionAccess funcAccess : functionAccess) {
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
        return list.stream().filter(perm -> perm.getFormCode().equalsIgnoreCase(form.getFormCode())).findFirst().isPresent();
    }
    @Override
    public User getUserById(long id) {
        return repository.findById(id).get();
    }
    @Override
    public List<ShopDTO> getShopByRole(Long roleId) {
        return getUserManageShops(roleId);
    }

    public Response<String> checkPassword(String password) {
        if (password.length() < 8 || password.length() > 20)
            return new Response<String>().withError(ResponseMessage.INVALID_PASSWORD_LENGTH);
        boolean containNum = false, containUpperCase = false, containLowerCase = false;

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
