package vn.viettel.authorization.service.impl;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.messaging.user.AdminUserCreateRequest;
import vn.viettel.authorization.repository.AdminUserRepository;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.dto.AdminPrivilegeDTO;
import vn.viettel.authorization.service.dto.PasswordResetDTO;
import vn.viettel.authorization.service.dto.authorization.AdminUserLoginDTO;
import vn.viettel.authorization.service.dto.user.AdminUserDTO;
import vn.viettel.authorization.service.dto.user.AdminUserEditDTO;
import vn.viettel.authorization.service.dto.user.UserAdminIndexDTO;
import vn.viettel.authorization.utils.UserUtils;
import vn.viettel.core.db.entity.AdminPrivilege;
import vn.viettel.core.db.entity.AdminUser;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.dto.user.AdminUserResponseDTO;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AdministratorAuthenticateServiceImpl extends BaseServiceImpl<AdminUser, AdminUserRepository> implements AdministratorAuthenticateService {

    private final int FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS = 1;
    private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

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
    AdminPrivilegeService adminPrivilegeService;

    /* LOGIN */
    @Override
    public Response<AdminUserLoginDTO> login(UserLoginRequest request) {
        Response<AdminUserLoginDTO> response = new Response<>();
        AdminUserLoginDTO userLogin = administratorLogin(request.getEmail(), request.getPassword());
        if (userLogin != null) {
            response.setData(userLogin);
            return response;
        }

        response.setFailure(ResponseMessage.LOGIN_FAILED);
        return response;
    }

    /* FORGOT PASSWORD */
    @Override
    public Response<String> forgotPassword(UserForgotPasswordRequest request) {
        Response<String> response = new Response<String>();
        AdminUser user = getAdministratorActivatedByEmail(request.getEmail());
        if (user == null) {
            response.setFailure(ResponseMessage.NOT_EXISTS_EMAIL);
            return response;
        }
        PasswordResetDTO passwordResetOld = passwordResetService.getByAdminUserId(user.getId()).getData();
        removeForgotPasswordToken(passwordResetOld);
        String token = UUID.randomUUID().toString();
        PasswordResetDTO passwordResetNew = UserUtils.createPasswordResetDTOForAdmin(token, user.getId());
        passwordResetService.save(passwordResetNew, PasswordResetDTO.class);
        emailService.sendEmailAdministratorForgotPassword(user, token);
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
        if (!isForgotPasswordTokenBelongsToAdministrator(passwordReset)) {
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
        if (!isForgotPasswordTokenBelongsToAdministrator(passwordReset)) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        Optional<AdminUser> optUser = repository.getAdminUserById(passwordReset.getAdminUserId());
        if (!optUser.isPresent()) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        removeForgotPasswordToken(passwordReset);
        AdminUser user = optUser.get();
        user.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        return response;
    }

    @Override
    public AdminUserLoginDTO administratorLogin(String email, String password) {
        AdminUser user = getAdministratorActivatedByEmail(email);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getEncryptedPassword())) {
            return null;
        }

        AdminPrivilege privilege = user.getPrivilege();
        String role = UserRole.ADMIN.value();
        Claims claims = ClaimsTokenBuilder.build(role)
                .withUserId(user.getId())
                .withIsSuperAdmin(privilege.isSuperAdmin())
                .withPrivilegeName(privilege.getPrivilegeName()).get();
        String token = jwtTokenCreate.createToken(claims);

        AdminUserLoginDTO userLogin = modelMapper.map(user, AdminUserLoginDTO.class);
        userLogin.setToken(token);
        userLogin.setPrivilegeName(privilege.getPrivilegeName());
        userLogin.setSuperAdmin(privilege.isSuperAdmin());
        userLogin.setRole(privilege.getPrivilegeName().toUpperCase());
        return userLogin;
    }

    @Override
    public Response<Page<UserAdminIndexDTO>> usersAdminIndex(String searchKeywords, Pageable pageable) {
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        Page<AdminUser> users = repository.getUserAdminIndexBySearchKeywords(searchKeywords, pageable);
        Page<UserAdminIndexDTO> usersIndex = users.map(this::mapUserToUserAdminIndexDTO);
        return new Response<Page<UserAdminIndexDTO>>().withData(usersIndex);
    }

    private UserAdminIndexDTO mapUserToUserAdminIndexDTO(AdminUser user) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        UserAdminIndexDTO userIndexDTO = modelMapper.map(user, UserAdminIndexDTO.class);
        AdminPrivilegeDTO adminPrivilegeDTO = new AdminPrivilegeDTO(user.getPrivilege().getId(),user.getPrivilege().getPrivilegeName(),user.getPrivilege().isSuperAdmin());
        userIndexDTO.setAdminPrivalige(adminPrivilegeDTO);
        return userIndexDTO;
    }

    @Override
    @Transactional
    public Response<AdminUserDTO> create(AdminUserCreateRequest request) {
        // Check email exist
        Optional<AdminUser> adminUser = repository.getAdminUserByEmail(request.getEmail());

        if (adminUser.isPresent()) {
            throw new ValidateException(ResponseMessage.ALREADY_EXISTS_EMAIL);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        AdminUser userParams = modelMapper.map(request, AdminUser.class);
        userParams.setEncryptedPassword(passwordEncoder.encode(request.getEncryptedPassword()));
        userParams.setPrivilege(getAdminPrivilege(request.getAdminPrivilageId()));
        AdminUser newRecord = repository.save(userParams);
        return new Response<AdminUserDTO>().withData(modelMapper.map(newRecord, AdminUserDTO.class));
    }

    @Override
    @Transactional
    public Response<AdminUserDTO> update(Long id, AdminUserCreateRequest request) {
        // Check email exist
        Optional<AdminUser> adminUser = repository.getAdminUserById(id);

        if (!adminUser.isPresent()) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        AdminUser userParams = adminUser.get();
        userParams.setName(request.getName());
        userParams.setPrivilege(getAdminPrivilege(request.getAdminPrivilageId()));
        userParams.setStatus(request.getStatus());
        if (!BCRYPT_PATTERN.matcher(request.getEncryptedPassword()).matches()) {
            userParams.setEncryptedPassword(passwordEncoder.encode(request.getEncryptedPassword()));
        }
        AdminUser newRecord = repository.save(userParams);
        return new Response<AdminUserDTO>().withData(modelMapper.map(newRecord, AdminUserDTO.class));
    }

    @Override
    @Transactional
    public Response<AdminUserDTO> delete(Long id) {
        // Check email exist
        Optional<AdminUser> adminUser = repository.getAdminUserById(id);

        if (!adminUser.isPresent()) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        AdminUser userParams = modelMapper.map(adminUser.get(),AdminUser.class);
        if (userParams.getPrivilege().isSuperAdmin()) {
            throw new ValidateException(ResponseMessage.USER_SUPER_ADMIN_CAN_NOT_DELETE);
        }
        userParams.setDeletedAt(LocalDateTime.now());
        AdminUser newRecord = repository.save(userParams);
        return new Response<AdminUserDTO>().withData(modelMapper.map(newRecord, AdminUserDTO.class));
    }

    @Override
    public Response<AdminUserEditDTO> edit(Long id) {
        AdminUser user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        AdminUserEditDTO data = modelMapper.map(user, AdminUserEditDTO.class);
        return new Response<AdminUserEditDTO>().withData(data);
    }

    /* GET Admin USER ACTIVATED BY EMAIL */
    private AdminUser getAdministratorActivatedByEmail(String email) {
        try {
            Optional<AdminUser> optUser = repository.getAdminUserByEmail(email);
            if (optUser.isPresent() && optUser.get().isStatus()) {
                return optUser.get();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /* REMOVE FORGOT PASSWORD TOKEN */
    private void removeForgotPasswordToken(PasswordResetDTO passwordReset) {
        if (passwordReset != null) {
            passwordReset.setDeletedAt(LocalDateTime.now());
            passwordResetService.save(passwordReset, PasswordResetDTO.class);
        }
    }

    /* CHECK FORGOT PASSWORD TOKEN BELONGS TO ADMIN */
    private boolean isForgotPasswordTokenBelongsToAdministrator(PasswordResetDTO passwordReset) {
        Long userId = passwordReset.getAdminUserId();
        try {
            Optional<AdminUser> optUser = repository.getAdminUserById(userId);
            return optUser.isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /* CHECK EXPIRED TOKEN */
    private boolean isExpiredToken(LocalDateTime dateCreateToken, int numberOfDayExpired) {
        return dateCreateToken.plusDays(numberOfDayExpired)
                .isBefore(LocalDateTime.now());
    }

    private AdminPrivilege getAdminPrivilege(Long id) {
        return adminPrivilegeService.getAdminPrivilege(id);
    }

    @Override
    public Response<AdminUserResponseDTO> getAdminById(Long id) {
        Response<AdminUserResponseDTO> result = new Response<>();
        return result.withData(modelMapper.map(repository.getAdminById(id), AdminUserResponseDTO.class));
    }
}
