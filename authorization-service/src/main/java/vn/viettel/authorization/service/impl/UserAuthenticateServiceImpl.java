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
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAuthenticateServiceImpl implements UserAuthenticateService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    PermissionRepository permissionRepo;

    @Autowired
    FunctionRepository funcRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Override
    public Response<LoginResponse> login(LoginRequest loginInfo) {
        Response<LoginResponse> response = new Response<>();
        User user = userRepo.findByUsername(loginInfo.getUsername());

        if (user == null) {
            response.setFailure(ResponseMessage.LOGIN_FAILED);
            return response;
        }
        if (!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) {
            response.setFailure(ResponseMessage.LOGIN_FAILED);
            return response;
        }

        if(!user.isActive()) {
            response.setFailure(ResponseMessage.USER_IS_NOT_ACTIVE);
            return response;
        }

        Claims claims = ClaimsTokenBuilder.build(getUserRoles(user.getId().intValue()))
                .withUserId(user.getId()).get();
        String token = jwtTokenCreate.createToken(claims);

        LoginResponse resData = new LoginResponse();

        resData.setUsername(user.getUsename());
        resData.setDOB(user.getDOB());
        resData.setEmail(user.getEmail());
        resData.setLastLoginDate(user.getLastLoginDate());
        resData.setFirstName(user.getFirstName());
        resData.setLastName(user.getLastName());
        resData.setPhoneNumber(user.getPhoneNumber());
        resData.setActive(user.isActive());
        resData.setRoles(getUserRoles(user.getId().intValue()));
        resData.setFunctions(getUserPermissions(getUserRoleId(user.getId().intValue())));
        response.setToken(token);
        response.setData(resData);
        return response;
    }

    @Override
    public Response<String> changePassword(ChangePasswordRequest request) {
        Response<String> response = new Response<>();

        User user = userRepo.findById(request.getUserId()).get();

        if (user == null) {
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return response.withError(ResponseMessage.USER_OLD_PASSWORD_NOT_CORRECT);
        }

        String securePassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(securePassword);
        userRepo.save(user);
        response.setData("change password success");
        return response;
    }

    // get List of role in String
    @Override
    public List<String> getUserRoles(int userId) {
        List<String> roles = new ArrayList<>();
        List<UserRole> userRoles = userRoleRepo.findByUserId(userId);
        for (UserRole role : userRoles) {
            if (roleRepo.findById((long) role.getRoleId()).isPresent())
                roles.add(roleRepo.findById((long) role.getRoleId()).get().getName());
        }
        return roles;
    }

    // get list of role_id
    @Override
    public List<Integer> getUserRoleId(int userId) {
        List<Integer> idList = new ArrayList<>();
        List<UserRole> userRoles = userRoleRepo.findByUserId(userId);
        for (UserRole role : userRoles) {
            if (roleRepo.findById((long) role.getRoleId()).isPresent())
                idList.add(roleRepo.findById((long) role.getRoleId()).get().getId().intValue());
        }
        return idList;
    }

    @Override
    public List<BigInteger> getFuncId(List<Integer> roleId) {
        return permissionRepo.getFunctionIdByRoles((roleId));
    }

    // get list of action_id that a user is allowed in a function
    @Override
    public List<BigInteger> getActionIdsAllow(List<Integer> roleIds, int funcId) {
        return permissionRepo.getActionsAllow(roleIds, funcId);
    }

    // set action allowed of a role in a function
    @Override
    public void setAction(FunctionResponse func, List<BigInteger> actId) {
        for (BigInteger id: actId) {
            if(id.intValue() == 1)
                func.setView(true);
            if(id.intValue() == 2)
                func.setCreate(true);
            if(id.intValue() == 3)
                func.setUpdate(true);
            if(id.intValue() == 4)
                func.setDelete(true);
        }
    }

    // set list of permission of a user
    @Override
    public List<FunctionResponse> getUserPermissions(List<Integer> roleIds) {
        List<FunctionResponse> result = new ArrayList<>();
        for(BigInteger id: getFuncId(roleIds)) {
            FunctionResponse func = new FunctionResponse();
            func.setId(id.intValue());
            if(funcRepo.findById(id.longValue()).isPresent())
                func.setName(funcRepo.findById(id.longValue()).get().getName());
            List<BigInteger> actIds = getActionIdsAllow(roleIds, id.intValue());
            setAction(func, actIds);
            result.add(func);
        }
        return result;
    }
}
