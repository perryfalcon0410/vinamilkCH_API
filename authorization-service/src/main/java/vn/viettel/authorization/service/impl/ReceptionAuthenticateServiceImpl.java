package vn.viettel.authorization.service.impl;

import vn.viettel.core.db.entity.commonEnum.Feature;
import vn.viettel.core.dto.company.CompanyFeatureListDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.repository.ManagementUserRepository;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.dto.PasswordResetDTO;
import vn.viettel.authorization.service.dto.authorization.ReceptionUserLoginDTO;
import vn.viettel.authorization.service.dto.authorization.SalonDTO;
import vn.viettel.authorization.service.dto.company.CompanyDTO;
import vn.viettel.authorization.service.feign.CompanyClient;
import vn.viettel.authorization.service.feign.SalonClient;
import vn.viettel.authorization.utils.UserUtils;
import vn.viettel.core.db.entity.ManagementUsers;
import vn.viettel.core.db.entity.PrivilegesUsers;
import vn.viettel.core.db.entity.Salon;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.FeatureNotAvailableException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.viettel.authorization.service.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReceptionAuthenticateServiceImpl extends BaseServiceImpl<ManagementUsers, ManagementUserRepository> implements ReceptionAuthenticateService {

    private final int FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS = 1;

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

    @Autowired
    SalonClient salonClient;

    @Autowired
    CompanyClient companyClient;


    @Override
    public Response<ReceptionUserLoginDTO> login(UserLoginRequest request) throws FeatureNotAvailableException {
        Response<ReceptionUserLoginDTO> response = new Response<>();
        ReceptionUserLoginDTO userLogin = receptionUserLogin(request.getEmail(), request.getPassword());
        if (userLogin != null) {
            response.setData(userLogin);
            return response;
        }

        response.setFailure(ResponseMessage.LOGIN_FAILED);
        return response;
    }

    @Override
    public Response<String> forgotPassword(UserForgotPasswordRequest request) throws FeatureNotAvailableException {
        Response<String> response = new Response<String>();
        ManagementUsers user = getReceptionUserActivatedByEmail(request.getEmail());
        if (user == null) {
            response.setFailure(ResponseMessage.NOT_EXISTS_EMAIL);
            return response;
        }
        checkReceptionFeature(user.getCompanyId());
        PasswordResetDTO passwordResetOld = passwordResetService.getByManagementUserId(user.getId()).getData();
        removeForgotPasswordToken(passwordResetOld);
        String token = UUID.randomUUID().toString();
        PasswordResetDTO passwordResetNew = UserUtils.createPasswordResetDTOForManagementUser(token, user.getId());
        passwordResetService.save(passwordResetNew, PasswordResetDTO.class);
        emailService.sendEmailManagementUserForgotReception(user, token);
        return response;
    }

    @Override
    public Response<String> checkValidResetPasswordToken(String token) {
        Response<String> response = new Response<String>();
        PasswordResetDTO passwordReset = passwordResetService.getByToken(token).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToReceptionUser(passwordReset)) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), FORGOT_PASSWORD_TOKEN_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        return response;
    }

    @Override
    public Response<String> updateForgotPassword(UserUpdateForgotPasswordRequest request) {
        Response<String> response = new Response<String>();
        PasswordResetDTO passwordReset = passwordResetService.getByToken(request.getToken()).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToReceptionUser(passwordReset)) {
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

    private void checkReceptionFeature(Long companyId) throws FeatureNotAvailableException {
        List<CompanyFeatureListDTO> featureList =
                companyClient.feignGetAvailableFeatureListByCompanyId(companyId);

        // check if the feature is currently on
        CompanyFeatureListDTO companyFeature =
                featureList.stream().filter(item -> item.getName().contentEquals(Feature.RECEPTION.getName())).findAny().orElse(null);
        if (companyFeature == null || companyFeature.getStatus()==null || !companyFeature.getStatus()) {
            throw new FeatureNotAvailableException(ResponseMessage.COMPANY_FEATURE_NOT_AVAILABLE);
        }
    }

    @Override
    public ReceptionUserLoginDTO receptionUserLogin(String email, String password) throws FeatureNotAvailableException {
        ManagementUsers user = managementUserService.getByEmailAndCompanyId(email);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getEncryptedPassword())) {
            return null;
        }

        checkReceptionFeature(user.getCompanyId());

        String role = UserRole.RECEPTION.value();
        List<PrivilegesUsers> privileges = privilegesUserService.getPrivilegesByManagementUserId(user.getId());
        PrivilegesUsers highestPrivilege = Collections.min(privileges, Comparator.comparing(c -> c.getManagementPrivilege().getId()));
        Claims claims = ClaimsTokenBuilder.build(role)
                .withUserId(user.getId()).withCompanyId(user.getCompanyId()).get();
        String token = jwtTokenCreate.createToken(claims);

        ReceptionUserLoginDTO userLogin = modelMapper.map(user, ReceptionUserLoginDTO.class);
        userLogin.setToken(token);
        userLogin.setRole(highestPrivilege.getManagementPrivilege().getPrivilegeName().toUpperCase());

        if(user.getCompanyId() != null) {
            CompanyDTO companyDTO = companyClient.getCompanyByCompanyId(user.getCompanyId()).getData();
            userLogin.setCompanySlug(companyDTO.getSlug());
        }

        if (user.getSalonId() != null) {
            Salon salon = salonClient.getExistedSalon(user.getSalonId());
            userLogin.setSalon(salon != null ? modelMapper.map(salon, SalonDTO.class) : null);
        }

        return userLogin;
    }

    private ManagementUsers getReceptionUserActivatedByEmail(String email) {
        Optional<ManagementUsers> optUser = repository.getUserByEmail(email);
        if (optUser.isPresent()) {
            if (optUser.get().isStatus()) {
                return optUser.get();
            }
        }
        return null;
    }

    private void removeForgotPasswordToken(PasswordResetDTO passwordReset) {
        if (passwordReset != null) {
            passwordReset.setDeletedAt(LocalDateTime.now());
            passwordResetService.save(passwordReset, PasswordResetDTO.class);
        }
    }

    private boolean isForgotPasswordTokenBelongsToReceptionUser(PasswordResetDTO passwordReset) {
        Long userId = passwordReset.getManagementUserId();
        Optional<ManagementUsers> optUser = repository.getManagementUserById(userId);
        return optUser.isPresent();
    }

    private boolean isExpiredToken(LocalDateTime dateCreateToken, int numberOfDayExpired) {
        return dateCreateToken.plusDays(numberOfDayExpired)
                .isBefore(LocalDateTime.now());
    }
}
