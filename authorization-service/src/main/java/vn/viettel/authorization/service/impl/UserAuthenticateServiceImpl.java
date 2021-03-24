package vn.viettel.authorization.service.impl;

import io.jsonwebtoken.Claims;
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
import vn.viettel.core.messaging.Response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Autowired
    ShopClient shopClient;

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
        } else {
            resData.setUsedRole(getUserUsedRole(user.getId()));
            resData.setFunctions(getPermission(getUserRoleId(user.getId()).get(0)));
            resData.setForms(getFormAction(getUserRoleId(user.getId()).get(0)));

            Claims claims = ClaimsTokenBuilder.build(getUserUsedRole(user.getId()))
                    .withUserId(user.getId()).get();
            String token = jwtTokenCreate.createToken(claims);
            response.setData(setLoginReturn(resData, user));
            response.setToken(token);
        }
        return response;
    }

    // allow user to choose one role to login if they have many roles and provide token
    @Override
    public Response<LoginResponse> login(LoginRequest loginInfo, long roleId) {
        Response<LoginResponse> response = checkLoginValid(loginInfo);

        if (response.getSuccess() == false) {
            response.setFailure(ResponseMessage.INVALID_USERNAME_OR_PASSWORD);
            return response;
        }
        User user;
        try {
            user = userRepo.findByUsername(loginInfo.getUsername());
        } catch (Exception e) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        String role = roleRepository.findById(roleId).get().getRoleName();

        Claims claims = ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).get();
        String token = jwtTokenCreate.createToken(claims);

        LoginResponse resData = new LoginResponse();

        resData.setUsedRole(role);
        resData.setFunctions(getPermission(roleId));
        resData.setForms(getFormAction(roleId));

        response.setToken(token);
        response.setData(setLoginReturn(resData, user));

        return response;
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

    public LoginResponse setLoginReturn(LoginResponse resData, User user) {
        Date date = new Date();
        Timestamp dateTime = new Timestamp(date.getTime());

        resData.setUsername(user.getUserAccount());
        resData.setEmail(user.getEmail());
        resData.setLastLoginDate(dateTime.toString());
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
    public List<Long> getUserShopList(List<Long> permissionIdList) {
        List<Long> shopIdList = new ArrayList<>();
        for (Long id : permissionIdList) {
            BigDecimal shopId = orgAccessRepository.finShopIdByPermissionId(id);
            if (shopId != null)
                if (!shopIdList.contains(shopId.longValue()))
                    shopIdList.add(shopId.longValue());
        }
        return shopIdList;
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

    // get list of permissionDTO to return in api
    public List<PermissionDTO> getPermission(Long roleId) {
        List<PermissionDTO> permissionDTOList = new ArrayList<>();
        List<Long> permissionIdList = getListPermissionId(roleId);
        List<Long> shopIdList = getUserShopList(permissionIdList);

        /* each shop may have plenty of org access
        -> get list permission of user in each shop
         */
        shopIdList.forEach(shopId -> {
            List<Permission> subPermissionList = new ArrayList<>(); // list permission of user in a shop
            for (Long permissionId : permissionIdList) {
                BigDecimal newShopId = orgAccessRepository.finShopIdByPermissionId(permissionId); // get shop id from permission id
                // any permission interact with the same shop will be add in the subPermissionList
                if (newShopId != null && newShopId.longValue() == shopId) {
                    Permission permission = permissionRepository.findById(permissionId).get();
                    subPermissionList.add(permission);
                }
            }
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setShopId(shopId);

            // check all actions from list permission of the same shop to set to PermissionDTO
            setAction(permissionDTO, subPermissionList);
            permissionDTOList.add(permissionDTO);
        });
        return permissionDTOList;
    }

    // set action allowed of a role in a function
    public void setAction(PermissionDTO permissionDTO, List<Permission> permissions) {
        permissions.forEach(permission -> {
            if (permission.getPermissionName().equalsIgnoreCase("view"))
                permissionDTO.setView(true);
            if (permission.getPermissionName().equalsIgnoreCase("create"))
                permissionDTO.setCreate(true);
            if (permission.getPermissionName().equalsIgnoreCase("update"))
                permissionDTO.setUpdate(true);
            if (permission.getPermissionName().equalsIgnoreCase("delete"))
                permissionDTO.setDelete(true);
        });
    }

    // get list of action in form that user allowed
    public List<FormDTO> getFormAction(Long roleId) {
        List<FormDTO> result = new ArrayList<>();
        List<Long> permissionIdList = getListPermissionId(roleId);

        // each permission will create a formDTO
        for (Long id : permissionIdList) {
            // if permission type is function/ report
            if (id != 2) {
                Form form;
                /* for each permission id
                -> get list of function access of that permission
                 */
                List<FunctionAccess> functionAccess = functionAccessRepository.findByPermissionId(id);
                if (functionAccess != null) {
                    for (FunctionAccess funcAccess : functionAccess) {
                        form = formRepository.findById(funcAccess.getFormId()).get();
                        /* for each function access
                         -> create a formDTO and add to formDTO list
                         */
                        if (form != null) {
                            FormDTO formDTO = new FormDTO(form.getUrl(),
                                    funcAccess.getControlId(),
                                    ShowStatus.getValueOf(funcAccess.getShowStatus()));
                            result.add(formDTO);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public User getUserById(long id) {
        User user = userRepo.findById(id).get();
        return user;
    }
}
