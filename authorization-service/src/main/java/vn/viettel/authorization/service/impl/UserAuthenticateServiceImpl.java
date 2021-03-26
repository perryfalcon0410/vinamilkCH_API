package vn.viettel.authorization.service.impl;

import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAuthenticateServiceImpl implements UserAuthenticateService {
    @Autowired
    private UserRepository userRepo;

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

    @Autowired
    ModelMapper modelMapper;

    /* check if user have more than 1 role, return user info only
    if user have only 1 role -> login success and provide token
     */
    @Override
    public Response<LoginResponse> preLogin(LoginRequest loginInfo) {
        Response<LoginResponse> response = checkLoginValid(loginInfo);
        if (response.getSuccess() == false) {
            response.setFailure(ResponseMessage.LOGIN_FAILED);
            return response;
        }

        User user = userRepo.findByUsername(loginInfo.getUsername());
        LoginResponse resData = new LoginResponse();

        if (getUserRoles(user.getId()).size() == 0) {
            response.setFailure(ResponseMessage.USER_ROLE_MUST_BE_NOT_BLANK);
            return response;
        }
        if (getUserRoles(user.getId()).size() > 1) {
            response.setData(setLoginReturn(resData, user));

            List<ShopDTO> shopDTOList = new ArrayList<>();
            List<RoleDTO> roleList = getUserRoles(user.getId());
            for (int i = 0; i < roleList.size(); i++) {
                List<Long> permissionIdList = getListPermissionId(roleList.get(i).getId());
                List<ShopDTO> shops = getUserManageShops(permissionIdList);
                // check and remove duplicate shop before addAll
                checkContain(shopDTOList, shops);
                shopDTOList.addAll(shops);
            }
            resData.setShops(shopDTOList);

        } else {
            Long roleId = getUserRoleId(user.getId()).get(0);

            resData.setUsedRole(getUserUsedRole(user.getId()));
            List<ShopDTO> shops = getUserManageShops(getListPermissionId(roleId));
            if (shops.size() > 1)
                resData.setShops(shops);
            else {
                resData.setUsedShop(shops.get(0));
                resData.setPermissions(getUserPermission(roleId, shops.get(0).getShopId()));

                Claims claims = ClaimsTokenBuilder.build(getUserUsedRole(user.getId()))
                        .withUserId(user.getId()).get();
                String token = jwtTokenCreate.createToken(claims);
                response.setData(setLoginReturn(resData, user));
                response.setToken(token);
            }
        }
        return response;
    }

    // allow user to choose one role to login if they have many roles and provide token
    @Override
    public Response<LoginResponse> login(LoginRequest loginInfo, long roleId, long shopId) {
        Response<LoginResponse> response = checkLoginValid(loginInfo);

        if (response.getSuccess() == false) {
            response.setFailure(ResponseMessage.INVALID_USERNAME_OR_PASSWORD);
            return response;
        }
        User user = userRepo.findByUsername(loginInfo.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        List<Long> userRoleList = getUserRoleId(user.getId());
        if (!userRoleList.contains(roleId))
            return response.withError(ResponseMessage.USER_ROLE_NOT_MATCH);
        String role = roleRepository.findById(roleId).get().getRoleName();

        LoginResponse resData = new LoginResponse();
        Shop shop = shopClient.getShopById(shopId).getData();
        if (shop == null)
            return response.withError(ResponseMessage.SHOP_NOT_FOUND);

        if (checkShopByRole(roleId, shopId)) {
            resData.setUsedShop(new ShopDTO(shopId, shop.getShopName()));
            resData.setPermissions(getUserPermission(roleId, shopId));
        } else
            return response.withError(ResponseMessage.SHOP_NOT_MATCH);
        resData.setUsedRole(role);

        Claims claims = ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).get();
        String token = jwtTokenCreate.createToken(claims);

        response.setToken(token);
        response.setData(setLoginReturn(resData, user));
        resData.setRoles(null);

        return response;
    }

    public boolean checkShopByRole(Long roleId, Long shopId) {
        List<ShopDTO> shops = getShopByRole(roleId);
        if (shops != null)
            for (ShopDTO shop : shops) {
                if (shop.getShopId() == shopId)
                    return true;
            }
        return false;
    }

    public Response<LoginResponse> checkLoginValid(LoginRequest loginInfo) {
        Response<LoginResponse> response = new Response<>();

        if (loginInfo == null) {
            response.setFailure(ResponseMessage.NO_CONTENT_PASSED);
            return response;
        }
        User user;
        try {
            user = userRepo.findByUsername(loginInfo.getUsername());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.LOGIN_FAILED);
            return response;
        }
        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            response.setFailure(ResponseMessage.LOGIN_FAILED);
            return response;
        }
        if (user.getStatus() == 0) {
            response.setFailure(ResponseMessage.USER_IS_NOT_ACTIVE);
            return response;
        }
//        if (shopClient.getShopById(user.getShopId()).getSuccess() == true) {
//            Shop shop = shopClient.getShopById(user.getShopId()).getData();
//            if (shop.getStatus() != 1) {
//                return response.withError(ResponseMessage.SHOP_IS_NOT_ACTIVE);
//            }
//        } else {
//            return response.withError(ResponseMessage.SHOP_NOT_FOUND);
//        }
        return response;
    }

    public Shop getShopById(Long shopId) {
        if (shopClient.getShopById(shopId).getSuccess() == true) {
            Shop shop = shopClient.getShopById(shopId).getData();
            return shop;
        }
        return null;
    }

    public LoginResponse setLoginReturn(LoginResponse resData, User user) {

        resData.setUsername(user.getUserAccount());
        resData.setEmail(user.getEmail());
        resData.setFirstName(user.getFirstName());
        resData.setLastName(user.getLastName());
        resData.setPhoneNumber(user.getPhone());
        resData.setActive(user.getStatus());
        resData.setRoles(getUserRoles(user.getId()));

        return resData;
    }

    @Override
    public Response<String> changePassword(ChangePasswordRequest request) {
        Response<String> response = new Response<>();

        if (request == null) {
            response.setFailure(ResponseMessage.NO_CONTENT_PASSED);
            return response;
        }
        User user;
        user = userRepo.findByUsername(request.getUsername());
        if (user == null)
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            return response.withError(ResponseMessage.USER_OLD_PASSWORD_NOT_CORRECT);

        if (request.getOldPassword().equals(request.getNewPassword()))
            return response.withError(ResponseMessage.DUPLICATE_PASSWORD);

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            return response.withError(ResponseMessage.CONFIRM_PASSWORD_NOT_CORRECT);

        String securePassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(securePassword);
        try {
            userRepo.save(user);
        } catch (Exception e) {
            response.setFailure(ResponseMessage.CHANGE_PASSWORD_FAIL);
            return response;
        }
        response.setData(ResponseMessage.SUCCESSFUL.toString());
        return response;
    }

    // get List of role in String
    public List<RoleDTO> getUserRoles(Long userId) {
        List<RoleDTO> roles = new ArrayList<>();
        List<RoleUser> userRoles = userRoleRepository.findByUserId(userId);
        for (RoleUser userRole : userRoles) {
            Role role = roleRepository.findById(userRole.getRoleId()).get();
            roles.add(new RoleDTO(role.getId(), role.getRoleName()));
        }
        return roles;
    }

    // get list of role id
    public List<Long> getUserRoleId(Long userId) {
        List<Long> idList = new ArrayList<>();
        List<RoleUser> userRoles = userRoleRepository.findByUserId(userId);
        for (RoleUser role : userRoles) {
            if (userRoleRepository.findById(role.getRoleId()).isPresent())
                idList.add(userRoleRepository.findById(role.getRoleId()).get().getId());
        }
        return idList;
    }

    // get list id of shops that user manage
    public List<Long> getUserShopIdList(List<Long> permissionIdList) {
        List<Long> shopIdList = new ArrayList<>();
        for (Long id : permissionIdList) {
            BigDecimal shopId = orgAccessRepository.finShopIdByPermissionId(id);
            if (shopId != null)
                if (!shopIdList.contains(shopId.longValue()))
                    shopIdList.add(shopId.longValue());
        }
        return shopIdList;
    }

    public List<ShopDTO> getUserManageShops(List<Long> permissionIdList) {
        List<ShopDTO> result = new ArrayList<>();
        List<Long> listShopId = getUserShopIdList(permissionIdList);

        for (Long shopId : listShopId) {
            Shop shop = shopClient.getShopById(shopId).getData();
            if (shop != null) {
                ShopDTO shopDTO = new ShopDTO(shop.getId(), shop.getShopName());
                result.add(shopDTO);
            }
        }
        return result;
    }

    // get list id of permission of a user
    public List<Long> getListPermissionId(Long roleId) {
        List<RolePermission> rolePermissionList = rolePermissionMapRepository.findByRoleId(roleId);
        List<Long> result = new ArrayList<>();
        rolePermissionList.forEach(e -> result.add(e.getPermissionId()));

        return result;
    }

    // get select role to login when user have plenty of roles
    public String getUserUsedRole(Long userId) {
        List<RoleUser> userRoles = userRoleRepository.findByUserId(userId);
        return roleRepository.findById(userRoles.get(0).getRoleId()).get().getRoleName();
    }

    public List<PermissionDTO> getUserPermission(Long roleId, Long shopId) {
        List<PermissionDTO> result = new ArrayList<>();
        List<Long> permissionInRole = getListPermissionId(roleId);

        for (Long id : permissionInRole) {
            Permission permission = permissionRepository.findById(id).get();
            if (permission != null) {
                if (permission.getIsFullPrivilege() == 1) {
                    result.addAll(getPermissionWhenFullPrivilege(id));

                }
                if (permission.getPermissionType() != 2) {
                    List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionId(id);
                    for (FunctionAccess funcAccess : functionAccess) {
                        Form form = formRepository.findById(funcAccess.getFormId()).get();

                        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                        PermissionDTO permissionDTO = modelMapper.map(form, PermissionDTO.class);

                        List<Control> controls = getAllControlInForm(form.getId());
                        List<ControlDTO> listControl = new ArrayList<>();

                        for (Control control : controls) {
                            ControlDTO controlDTO = modelMapper.map(control, ControlDTO.class);
                            controlDTO.setShowStatus(ShowStatus.getValueOf(funcAccess.getShowStatus()));
                            listControl.add(controlDTO);
                        }
                        permissionDTO.setControls(listControl);
                        result.add(permissionDTO);
                    }
                }
            }
        }
        return result;
    }

    public List<PermissionDTO> getPermissionWhenFullPrivilege(Long permissionId) {
        List<PermissionDTO> result = new ArrayList<>();

        List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionId(permissionId);
        for (FunctionAccess funcAccess : functionAccess) {

            Form form = formRepository.findById(funcAccess.getFormId()).get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PermissionDTO permissionDTO = modelMapper.map(form, PermissionDTO.class);

            List<Control> controlList = getAllControlInForm(funcAccess.getFormId());
            List<ControlDTO> listControl = new ArrayList<>();

            for (Control control : controlList) {
                ControlDTO controlDTO = modelMapper.map(control, ControlDTO.class);
                listControl.add(controlDTO);
                controlDTO.setShowStatus(ShowStatus.getValueOf(funcAccess.getShowStatus()));

                permissionDTO.setControls(listControl);

                result.add(permissionDTO);
            }
        }
        return result;
    }

    public List<Control> getAllControlInForm(Long formId) {
        List<Control> controlList = controlRepository.findByFormId(formId);
        return controlList;
    }

    public void checkContain(List<ShopDTO> mainList, List<ShopDTO> subList) {
        for (ShopDTO shopMain : mainList) {
            subList.removeIf(shopSub -> shopMain.getShopId() == shopSub.getShopId());
        }
    }

    @Override
    public User getUserById(long id) {
        User user = userRepo.findById(id).get();
        return user;
    }

    @Override
    public List<ShopDTO> getShopByRole(Long roleId) {
        List<Long> listPermissionId = getListPermissionId(roleId);
        return getUserManageShops(listPermissionId);
    }
}
