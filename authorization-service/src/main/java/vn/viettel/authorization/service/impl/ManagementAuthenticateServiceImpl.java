package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.dto.company.CompanyDTO;
import vn.viettel.authorization.service.feign.CompanyClient;
import vn.viettel.authorization.service.feign.SalonClient;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserRegisterRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.messaging.user.ManagementUserCreateRequest;
import vn.viettel.authorization.repository.ManagementUserRepository;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.dto.ManagementPrivilegeDTO;
import vn.viettel.authorization.service.dto.PasswordResetDTO;
import vn.viettel.authorization.service.dto.PrivilegesUsersDTO;
import vn.viettel.authorization.service.dto.authorization.UserLoginDTO;
import vn.viettel.authorization.service.dto.user.ManagementUserDTO;
import vn.viettel.authorization.service.dto.user.ManagementUserEditDTO;
import vn.viettel.authorization.service.dto.user.ManagementUserIndexDTO;
import vn.viettel.authorization.utils.UserUtils;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.db.entity.status.UserStatus;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.authorization.service.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ManagementAuthenticateServiceImpl extends BaseServiceImpl<ManagementUsers, ManagementUserRepository> implements ManagementAuthenticateService {

    private final int FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS = 1;
    private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    @Autowired
    RoleService roleService;

    @Autowired
    CompanyClient companyClient;

    @Autowired
    SalonClient salonClient;

    @Autowired
    ManagementPrivilegeService managementPrivilegeService;

    @Autowired
    PayjpService payjpService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordResetService passwordResetService;

    @Autowired
    EmailService emailService;

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Autowired
    ManagementUserService managementUserService;

    @Autowired
    PrivilegesUserService privilegesUserService;

    /* REGISTER */
    @Override
    public Response<String> register(UserRegisterRequest request) {
        Response<String> response = new Response<>();
//
//        if (isEmailExist(request.getEmail())) {
//            response.setFailure(ResponseMessage.ALREADY_EXISTS_EMAIL);
//            return response;
//        }
//
//        User user = mapDataRequestToRegisterUser(request);
//        repository.save(user);
        return response;
    }

    /* LOGIN */
    @Override
    public Response<UserLoginDTO> login(UserLoginRequest request) {
        Response<UserLoginDTO> response = new Response<>();
        UserLoginDTO userLogin = managementUserLogin(request.getEmail(), request.getPassword());
        if (userLogin != null) {
            response.setData(userLogin);
            return response;
        }

//        userLogin = distributorLogin(request.getEmail(), request.getPassword());
//        if (userLogin != null) {
//            response.setData(userLogin);
//            return response;
//        }
//
//        userLogin = managementUserLogin(request.getEmail(), request.getPassword());
//        if (userLogin != null) {
//            response.setData(userLogin);
//            return response;
//        }

        response.setFailure(ResponseMessage.LOGIN_FAILED);
        return response;
    }

    /* FORGOT PASSWORD */
    @Override
    public Response<String> forgotPassword(UserForgotPasswordRequest request) {
        Response<String> response = new Response<String>();
        ManagementUsers user = getManagementUserActivatedByEmail(request.getEmail());
        if (user == null) {
            response.setFailure(ResponseMessage.NOT_EXISTS_EMAIL);
            return response;
        }
        PasswordResetDTO passwordResetOld = passwordResetService.getByManagementUserId(user.getId()).getData();
        removeForgotPasswordToken(passwordResetOld);
        String token = UUID.randomUUID().toString();
        PasswordResetDTO passwordResetNew = UserUtils.createPasswordResetDTOForManagementUser(token, user.getId());
        passwordResetService.save(passwordResetNew, PasswordResetDTO.class);
        emailService.sendEmailManagementUserForgotPassword(user, token);
        return response;
    }

    /* CHECK VALID RESET PASSWORD TOKEN */
    @Override
    public Response<String> checkValidResetPasswordToken(String token) {
        Response<String> response = new Response<String>();
        PasswordResetDTO passwordReset = passwordResetService.getByToken(token).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToManagementUser(passwordReset)) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        return response;
    }

    /* UPDATE FORGOT PASSWORD */
    @Override
    public Response<String> updateForgotPassword(UserUpdateForgotPasswordRequest request) {
        Response<String> response = new Response<String>();
        PasswordResetDTO passwordReset = passwordResetService.getByToken(request.getToken()).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToManagementUser(passwordReset)) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        Optional<ManagementUsers> optManagementUsers = repository.getManagementUserById(passwordReset.getManagementUserId());
        if (!optManagementUsers.isPresent()) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        removeForgotPasswordToken(passwordReset);
        ManagementUsers user = optManagementUsers.get();
        user.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        return response;
    }

    @Override
    public UserLoginDTO managementUserLogin(String email, String password) {
        ManagementUsers user = managementUserService.getByEmailAndCompanyId(email);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getEncryptedPassword())) {
            return null;
        }

        String role = UserRole.MANAGEMENT.value();
        List<PrivilegesUsers> privileges = privilegesUserService.getPrivilegesByManagementUserId(user.getId());
        List<String> privilegesNames = privileges.stream().map(p -> p.getManagementPrivilege().getPrivilegeName()).collect(Collectors.toList());
        PrivilegesUsers highestPrivilege = Collections.min(privileges, Comparator.comparing(c -> c.getManagementPrivilege().getId()));
        Claims claims = ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).withCompanyId(user.getCompanyId()).withPrivilegeNames(privilegesNames).get();
        String token = jwtTokenCreate.createToken(claims);

        UserLoginDTO userLogin = modelMapper.map(user, UserLoginDTO.class);
        CompanyDTO companyDTO = companyClient.getCompanyByCompanyId(user.getCompanyId()).getData();
        userLogin.setCompanyName(companyDTO.getName());
        userLogin.setPrivilegesNames(privilegesNames);
        userLogin.setToken(token);
        userLogin.setRole(highestPrivilege.getManagementPrivilege().getPrivilegeName().toUpperCase());
        if (user.getSalonId()!=null) {
            Salon salon = salonClient.getExistedSalon(user.getSalonId());
            userLogin.setSalonId(user.getSalonId());
            userLogin.setSalonName(salon.getName());
        }
        return userLogin;
    }

//    public UserLoginDTO distributorLogin(String email, String password) {
//        User user = getDistributorActivatedByEmail(email);
//        if (user == null) {
//            return null;
//        }
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            return null;
//        }
//
//        String role = UserRole.DISTRIBUTOR.value();
//        Claims claims = ClaimsTokenBuilder.build(role)
//                .withUserId(user.getId()).get();
//        String token = jwtTokenCreate.createToken(claims);
//
//        UserLoginDTO userLogin = modelMapper.map(user, UserLoginDTO.class);
//        userLogin.setToken(token);
//        userLogin.setRole(role);
//        return userLogin;
//    }

    /* ==================== PRIVATE METHOD ==================== */
//    private UserLoginDTO managementUserLogin(String email, String password) {
//        ManagementUsers user = managementUserService.getByEmail(email);
//        if (user == null) {
//            return null;
//        }
//
//        if (!passwordEncoder.matches(password, user.getEncryptedPassword())) {
//            return null;
//        }
//
//        if (this.isExpiredPasswordEmployee(user)) {
//            throw new ValidateException(ResponseMessage.USER_PASSWORD_IS_EXPIRED);
//        }
//
//        Role role = roleService.getById(user.getRoleId());
//        String roleName = role.getRoleName();
//
//        Claims claims = ClaimsTokenBuilder.build(roleName)
//                .withUserId(user.getUserId())
//                .withObject(user.getObject())
//                .withObjectId(user.getObjectId()).get();
//        String token = jwtTokenCreate.createToken(claims);
//
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//
//        UserLoginDTO userLogin = modelMapper.map(user, UserLoginDTO.class);
//        userLogin.setToken(token);
//        userLogin.setRole(roleName);
//        return userLogin;
//    }

//    private boolean isEmailExist(String email) {
//        User user = getShopOwnerActivatedByEmail(email);
//        ManagementUsers managementUsers = managementUserService.getByEmail(email);
//        if (user != null || managementUsers != null) {
//            return true;
//        }
//        return false;
//    }

    /* GET CUSTOMER USER BY EMAIL AND ROLE ID */
    //    private User getShopOwnerUserByEmail(String email) {
    //        Long RoleId = getShopOwnerRole().getId();
    //        Optional<User> optUser = repository.getByEmailAndRoleId(email, RoleId);
    //        if (optUser.isPresent()) {
    //            return optUser.get();
    //        }
    //        return null;
    //    }

    /* GET CUSTOMER USER ACTIVATED BY EMAIL */
    private ManagementUsers getManagementUserActivatedByEmail(String email) {
        Optional<ManagementUsers> optUser = repository.getUserByEmail(email);
        if (optUser.isPresent()) {
            if (optUser.get().isStatus()) {
                return optUser.get();
            }
        }
        return null;
    }

//    private User getDistributorActivatedByEmail(String email) {
//        Long RoleId = roleService.getByRoleName(UserRole.DISTRIBUTOR).getId();
//        Optional<User> optUser = repository.getByEmailAndRoleId(email, RoleId);
//        if (optUser.isPresent()) {
//            if (UserStatus.ACTIVE.value().equals(optUser.get().getStatus())) {
//                return optUser.get();
//            }
//        }
//        return null;
//    }

    /* GET CUSTOMER ROLE */
    private Role getShopOwnerRole() {
        return roleService.getByRoleName(UserRole.SHOP_OWNER);
    }

    /* MAP DATA REQUEST TO REGISTER USER */
    private User mapDataRequestToRegisterUser(UserRegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE.status());
        user.setRole(getShopOwnerRole());
        return user;
    }

    /* REMOVE FORGOT PASSWORD TOKEN */
    private void removeForgotPasswordToken(PasswordResetDTO passwordReset) {
        if (passwordReset != null) {
            passwordReset.setDeletedAt(LocalDateTime.now());
            passwordResetService.save(passwordReset, PasswordResetDTO.class);
        }
    }

    /* CHECK FORGOT PASSWORD TOKEN BELONGS TO CUSTOMER */
    private boolean isForgotPasswordTokenBelongsToManagementUser(PasswordResetDTO passwordReset) {
        Long userId = passwordReset.getManagementUserId();
        Optional<ManagementUsers> optUser = repository.getManagementUserById(userId);
        return optUser.isPresent();
    }

    /* CHECK EXPIRED TOKEN */
    private boolean isExpiredToken(LocalDateTime dateCreateToken, int numberOfDayExpired) {
        return dateCreateToken.plusDays(numberOfDayExpired)
                .isBefore(LocalDateTime.now());
    }

    private boolean isExpiredPasswordEmployee(ManagementUsers user) {
//        Role role = roleService.getById(user.getRoleId());
//        if (role.getRoleName().equals(UserRole.SHOP_EMPLOYEE.value())
//                || role.getRoleName().equals(UserRole.GROUP_EMPLOYEE.value())) {
//            int numberExpiration = user.getNumberOfDateExpiration();
//            LocalDate dateExpired = user.getUpdatedAt().toLocalDate().plusDays(numberExpiration);
//            return dateExpired.isBefore(LocalDate.now());
//        }
        return false;
    }

    @Override
    public Response<Page<ManagementUserIndexDTO>> usersManagementIndex(String searchKeywords, Long idCompany, Pageable pageable) {
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        Page<ManagementUsers> users = repository.getUserManagementIndexBySearchKeywords(searchKeywords, idCompany, pageable);
        Page<ManagementUserIndexDTO> usersIndex = users.map(this::mapUserToUserManagementIndexDTO);
        return new Response<Page<ManagementUserIndexDTO>>().withData(usersIndex);
    }

    private ManagementUserIndexDTO mapUserToUserManagementIndexDTO(ManagementUsers user) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ManagementUserIndexDTO userIndexDTO = modelMapper.map(user, ManagementUserIndexDTO.class);
        List<PrivilegesUsers> privileges = privilegesUserService.getPrivilegesByManagementUserId(user.getId());
        List<ManagementPrivilegeDTO> privilegeDtos = privileges.stream()
                .map(p -> new ManagementPrivilegeDTO(p.getManagementPrivilege().getId(), p.getManagementPrivilege().getPrivilegeName(), p.getManagementPrivilege().isSuperAdmin()))
                .collect(Collectors.toList());
        userIndexDTO.setPrivileges(privilegeDtos);
        return userIndexDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<ManagementUserDTO> create(ManagementUserCreateRequest request) {
        // Check email exist
        Optional<ManagementUsers> adminUser = repository.getManagementUserByEmailAndCompanyId(request.getEmail(), request.getCompanyId());

        if (adminUser.isPresent()) {
            throw new ValidateException(ResponseMessage.ALREADY_EXISTS_EMAIL);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ManagementUsers userParams = modelMapper.map(request, ManagementUsers.class);
        userParams.setEncryptedPassword(passwordEncoder.encode(request.getEncryptedPassword()));

        ManagementUsers newRecord = repository.save(userParams);
        request.getManagementPrivilageIds().forEach(aLong -> {
            PrivilegesUsers privilegesUsers = new PrivilegesUsers();
            privilegesUsers.setManagementPrivilege(managementPrivilegeService.getById(aLong));
            privilegesUsers.setManagementUsers(newRecord);
            PrivilegesUsersDTO privilegesUsersDTO = modelMapper.map(privilegesUsers, PrivilegesUsersDTO.class);
            privilegesUserService.save(privilegesUsersDTO,PrivilegesUsersDTO.class);
        });
        return new Response<ManagementUserDTO>().withData(modelMapper.map(newRecord, ManagementUserDTO.class));
    }

    @Override
    @Transactional
    public Response<ManagementUserDTO> update(Long id, ManagementUserCreateRequest request) {
        // Check email exist
        Optional<ManagementUsers> adminUser = repository.getManagementUserById(id);

        if (!adminUser.isPresent()) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ManagementUsers userParams = adminUser.get();
        userParams.setName(request.getName());
        userParams.setEmail(request.getEmail());
        userParams.setCompanyId(request.getCompanyId());
        userParams.setStatus(request.getStatus());
        if (!BCRYPT_PATTERN.matcher(request.getEncryptedPassword()).matches()) {
            userParams.setEncryptedPassword(passwordEncoder.encode(request.getEncryptedPassword()));
        }
        ManagementUsers newRecord = repository.save(userParams);
        privilegesUserService.deleteByUserId(newRecord.getId());

        request.getManagementPrivilageIds().forEach(aLong -> {
            PrivilegesUsers privilegesUsers = new PrivilegesUsers();
            privilegesUsers.setManagementPrivilege(managementPrivilegeService.getById(aLong));
            privilegesUsers.setManagementUsers(newRecord);
            PrivilegesUsersDTO privilegesUsersDTO = modelMapper.map(privilegesUsers, PrivilegesUsersDTO.class);
            privilegesUserService.save(privilegesUsersDTO,PrivilegesUsersDTO.class);
        });
        return new Response<ManagementUserDTO>().withData(modelMapper.map(newRecord, ManagementUserDTO.class));
    }

    @Override
    @Transactional
    public Response<ManagementUserDTO> delete(Long id) {
        // Check email exist
        Optional<ManagementUsers> adminUser = repository.getManagementUserById(id);

        if (!adminUser.isPresent()) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        ManagementUsers userParams = modelMapper.map(adminUser.get(),ManagementUsers.class);
        PrivilegesUsers privilegesUsers = privilegesUserService.getByPrivilegeIdAndUserId(1L,userParams.getId());
        if (privilegesUsers.getId() > 0) {
            throw new ValidateException(ResponseMessage.USER_SUPER_ADMIN_CAN_NOT_DELETE);
        }
        userParams.setDeletedAt(LocalDateTime.now());
        ManagementUsers newRecord = repository.save(userParams);
        return new Response<ManagementUserDTO>().withData(modelMapper.map(newRecord, ManagementUserDTO.class));
    }

    @Override
    public Response<ManagementUserEditDTO> edit(Long id) {
        ManagementUsers user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        ManagementUserEditDTO data = modelMapper.map(user, ManagementUserEditDTO.class);
        List<PrivilegesUsers> privileges = privilegesUserService.getPrivilegesByManagementUserId(user.getId());
        List<ManagementPrivilegeDTO> privilegeDtos = privileges.stream()
                .map(p -> new ManagementPrivilegeDTO(p.getManagementPrivilege().getId(), p.getManagementPrivilege().getPrivilegeName(), p.getManagementPrivilege().isSuperAdmin()))
                .collect(Collectors.toList());
        data.setPrivileges(privilegeDtos);
        return new Response<ManagementUserEditDTO>().withData(data);
    }

    @Override
    public Response<ManagementUserEditDTO> getUserAdminByCompanyId(Long id) {
        ManagementUsers user = repository.getUserAdminByCompanyId(id);
        if (user == null) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        ManagementUserEditDTO data = modelMapper.map(user, ManagementUserEditDTO.class);
        List<PrivilegesUsers> privileges = privilegesUserService.getPrivilegesByManagementUserId(user.getId());
        List<ManagementPrivilegeDTO> privilegeDtos = privileges.stream()
                .map(p -> new ManagementPrivilegeDTO(p.getManagementPrivilege().getId(), p.getManagementPrivilege().getPrivilegeName(), p.getManagementPrivilege().isSuperAdmin()))
                .collect(Collectors.toList());
        data.setPrivileges(privilegeDtos);
        return new Response<ManagementUserEditDTO>().withData(data);
    }
}
