package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.messaging.customer.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserLoginForViewPageRequest;
import vn.viettel.authorization.repository.UserRepository;
import vn.viettel.authorization.security.ClaimsTokenBuilder;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.*;
import vn.viettel.authorization.service.dto.PasswordResetDTO;
import vn.viettel.authorization.service.dto.authorization.CustomerLoginDTO;
import vn.viettel.authorization.service.dto.authorization.UserLoginDTO;
import vn.viettel.authorization.service.dto.group.GroupDTO;
import vn.viettel.authorization.service.dto.shop.ShopDTO;
import vn.viettel.authorization.service.feign.CompanyClient;
import vn.viettel.authorization.service.feign.GroupClient;
import vn.viettel.authorization.service.feign.ObjectSettingClient;
import vn.viettel.authorization.service.feign.ShopClient;
import vn.viettel.core.db.entity.CustomerInformation;
import vn.viettel.core.db.entity.Role;
import vn.viettel.core.db.entity.User;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.db.entity.status.UserStatus;
import vn.viettel.core.dto.ObjectSettingDTO;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.ResponseMessage;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerAuthenticateServiceImpl extends BaseServiceImpl<User, UserRepository> implements CustomerAuthenticateService {

    private final int CUSTOMER_TOKEN_ACTIVE_EXPIRED_DAYS = 1;
    private final int CUSTOMER_FORGOT_PASSWORD_EXPIRED_DAYS = 1;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RoleService roleService;

    @Autowired
    EmailService emailService;

    @Autowired
    ShopClient shopClient;

    @Autowired
    GroupClient groupClient;

    @Autowired
    CompanyClient companyClient;

    @Autowired
    ObjectSettingClient objectSettingClient;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordResetService passwordResetService;

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Autowired
    ManagementAuthenticateService managementAuthenticateService;

    @Override
    public Response<String> register(CustomerRegisterRequest request) {
        Response<String> response = new Response<>();

            User user = getCustomerUserByEmail(request.getEmail());
        if (user != null && UserStatus.ACTIVE.value().equals(user.getStatus())) {
            return response.withError(ResponseMessage.ALREADY_EXISTS_EMAIL);
        }

        String objectCode = "";

        switch (request.getObject()) {
            case GROUP: {
                objectCode = this.getGroupCodeByGroupId(request.getObjectId());
                if (StringUtils.isBlank(objectCode)) {
                    return response.withError(ResponseMessage.GROUP_DOES_NOT_EXIST);
                }
                break;
            }
            case SHOP: {
                objectCode = this.getShopCodeByShopId(request.getObjectId());
                if (StringUtils.isBlank(objectCode)) {
                    return response.withError(ResponseMessage.SALON_DOES_NOT_EXIST);
                }
                break;
            }
//            case COMPANY: {
//                objectCode = this.getCompanyCodeByCompanyId(request.getObjectId());
//                if (StringUtils.isBlank(objectCode)) {
//                    return response.withError(ResponseMessage.COMPANY_DOES_NOT_EXIST);
//                }
//                break;
//            }
        }

        User userRecord;

        if (user == null) {
            userRecord = this.createProvisionalRegisterUser(request);
        } else {
            userRecord = this.updateProvisionalRegisterUser(request);
        }

        emailService.sendEmailCustomerUserActive(userRecord, objectCode);
        return response;
    }

    /* CHECK VALID ACTIVATION CODE */
    @Override
    public Response<String> checkValidActivationCode(@Valid CustomerValidActivationCodeRequest request) {
        Response<String> response = new Response<String>();
        Optional<User> optUser = repository.getByActivationCode(request.getToken());
        if (!optUser.isPresent()) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(optUser.get().getUpdatedAt(), CUSTOMER_TOKEN_ACTIVE_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_ACTIVATION_TOKEN_HAS_EXPIRED);
            return response;
        }
        return response;
    }

    /* FORGOT PASSWORD */
    @Override
    public Response<String> forgotPassword(CustomerForgotPasswordRequest request) {
        Response<String> response = new Response<>();

        User user = getCustomerUserActivatedByEmail(request.getEmail());
        if (user == null) {
            return response.withError(ResponseMessage.NOT_EXISTS_EMAIL);
        }

        PasswordResetDTO passwordResetOld = passwordResetService.getByUserId(user.getId()).getData();
        removeForgotPasswordToken(passwordResetOld);

        String objectCode = "";

        switch (request.getObject()) {
            case GROUP: {
                objectCode = this.getGroupCodeByGroupId(request.getObjectId());
                if (StringUtils.isBlank(objectCode)) {
                    return response.withError(ResponseMessage.GROUP_DOES_NOT_EXIST);
                }
                break;
            }
            case SHOP: {
                objectCode = this.getShopCodeByShopId(request.getObjectId());
                if (StringUtils.isBlank(objectCode)) {
                    return response.withError(ResponseMessage.SALON_DOES_NOT_EXIST);
                }
                break;
            }
//            case COMPANY: {
//                objectCode = this.getCompanyCodeByCompanyId(request.getObjectId());
//                if (StringUtils.isBlank(objectCode)) {
//                    return response.withError(ResponseMessage.COMPANY_DOES_NOT_EXIST);
//                }
//                break;
//            }
        }

        String token = UUID.randomUUID().toString();
        PasswordResetDTO passwordResetNew = null;
        passwordResetService.save(passwordResetNew, PasswordResetDTO.class);
        emailService.sendEmailCustomerUserForgotPassword(user, token, objectCode);

        return response;
    }

    /* CHECK VALID FORGOT PASSWORD TOKEN */
    @Override
    public Response<String> checkValidForgotPasswordToken(String token) {
        Response<String> response = new Response<String>();

        PasswordResetDTO passwordReset = passwordResetService.getByToken(token).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToCustomer(passwordReset)) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), CUSTOMER_FORGOT_PASSWORD_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        return response;
    }

    /* UPDATE FORGOT PASSWORD */
    @Override
    public Response<String> updateForgotPassword(CustomerUpdatePasswordRequest request) {
        Response<String> response = new Response<String>();
        PasswordResetDTO passwordReset = passwordResetService.getByToken(request.getToken()).getData();
        if (passwordReset == null) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (!isForgotPasswordTokenBelongsToCustomer(passwordReset)) {
            response.setFailure(ResponseMessage.INVALID_TOKEN);
            return response;
        }
        if (isExpiredToken(passwordReset.getCreatedAt(), CUSTOMER_FORGOT_PASSWORD_EXPIRED_DAYS)) {
            response.setFailure(ResponseMessage.USER_FORGOT_PASSWORD_TOKEN_HAS_EXPIRED);
            return response;
        }
        Optional<User> optUser = repository.getById(passwordReset.getMemberId());
        if (!optUser.isPresent()) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        removeForgotPasswordToken(passwordReset);
        User user = optUser.get();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        return response;
    }

    /* CUSTOMER LOGIN */
    @Override
    public Response<CustomerLoginDTO> login(CustomerLoginRequest request) {
        Response<CustomerLoginDTO> response = new Response<CustomerLoginDTO>();

        User user = getCustomerUserActivatedByEmail(request.getEmail());
        if (user == null) {
            response.setFailure(ResponseMessage.LOGIN_FAILED);
            return response;
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setFailure(ResponseMessage.LOGIN_FAILED);
            return response;
        }

        Claims claims = ClaimsTokenBuilder.build(UserRole.CUSTOMER.value())
                .withUserId(user.getId()).get();
        String token = jwtTokenCreate.createToken(claims);

        CustomerLoginDTO resData = modelMapper.map(user, CustomerLoginDTO.class);
        resData.setToken(token);
        resData.setRole(user.getRole().getRoleName());
        resData.setName(user.getCustomerInfomation().getName());
        resData.setCustomerNumber(user.getCustomerInfomation().getCustomerNumber());
        resData.setPhone(user.getCustomerInfomation().getTelephone());
        response.setData(resData);
        return response;
    }

    /* CHANGE PASSWORD */
    @Override
    public Response<String> changePassword(CustomerChangePasswordRequest request) {
        Response<String> response = new Response<>();

        User user = getCustomerUserActivatedByUserId(getUserId());
        if (user == null) {
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        boolean isNotCorrectOldPassword = !passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (isNotCorrectOldPassword) {
            return response.withError(ResponseMessage.USER_OLD_PASSWORD_NOT_CORRECT);
        }

        String securePassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(securePassword);
        repository.save(user);
        return response;
    }

    @Override
    public Response<UserLoginDTO> loginForViewPage(UserLoginForViewPageRequest request) {
//        Response<UserLoginDTO> response = new Response<>();
//
//        UserLoginDTO userLogin = managementAuthenticateService.shopOwnerLogin(request.getEmail(), request.getPassword());
//        if (userLogin == null) {
//            return response.withError(ResponseMessage.LOGIN_FAILED);
//        }
//
//        Long uid = userLogin.getId();
//        switch (request.getObject()) {
//            case GROUP: {
//                GroupDTO group = groupClient.getGroupByIdAndUserId(request.getObjectId(), uid).getData();
//                if (group == null) {
//                    return response.withError(ResponseMessage.LOGIN_FAILED);
//                }
//                break;
//            }
//            case SHOP: {
//                ShopDTO shop = shopClient.getShopByIdAndUserId(request.getObjectId(), uid).getData();
//                if (shop == null) {
//                    return response.withError(ResponseMessage.LOGIN_FAILED);
//                }
//                break;
//            }
//            case COMPANY: {
//                CompanyDTO company = companyClient.getCompanyByIdAndUserId(request.getObjectId(), uid).getData();
//                if (company == null) {
//                    return response.withError(ResponseMessage.LOGIN_FAILED);
//                }
//                break;
//            }
//        }
//
//        return response.withData(userLogin);
        return null;
    }

    /* ==================== PRIVATE METHOD ==================== */
    private User getCustomerUserByEmail(String email) {
        Long customerRoleId = getCustomerRole().getId();
        Optional<User> optUser = repository.getByEmailAndRoleId(email, customerRoleId);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        return null;
    }

    private User getCustomerUserActivatedByEmail(String email) {
        Long customerRoleId = getCustomerRole().getId();
        Optional<User> optUser = repository.getByEmailAndRoleId(email, customerRoleId);
        if (optUser.isPresent()) {
            if (UserStatus.ACTIVE.value().equals(optUser.get().getStatus())) {
                return optUser.get();
            }
        }
        return null;
    }

    private User getCustomerUserActivatedByUserId(Long id) {
        Long customerRoleId = getCustomerRole().getId();
        Optional<User> optUser = repository.getByIdAndRoleId(id, customerRoleId);
        if (optUser.isPresent()) {
            if (UserStatus.ACTIVE.value().equals(optUser.get().getStatus())) {
                return optUser.get();
            }
        }
        return null;
    }

    private Role getCustomerRole() {
        return roleService.getByRoleName(UserRole.CUSTOMER);
    }

    private String getShopCodeByShopId(Long shopId) {
        ObjectSettingDTO objectSetting = objectSettingClient.getByObjectAndObjectId(Object.SHOP.getId(), shopId);
        if (objectSetting != null) {
            return objectSetting.getCustomDomainName();
        }
        ShopDTO shop = shopClient.findById(shopId).getData();
        if (shop == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.defaultIfBlank(shop.getShopCode(), StringUtils.EMPTY);
    }

    private String getGroupCodeByGroupId(Long groupId) {
        ObjectSettingDTO objectSetting = objectSettingClient.getByObjectAndObjectId(Object.GROUP.getId(), groupId);
        if (objectSetting != null) {
            return objectSetting.getCustomDomainName();
        }
        GroupDTO group = groupClient.getGroupByGroupId(groupId).getData();
        if (group == null) {
            return StringUtils.EMPTY;
        }
        return StringUtils.defaultIfBlank(group.getGroupCode(), StringUtils.EMPTY);
    }

//    private String getCompanyCodeByCompanyId(Long companyId) {
//        ObjectSettingDTO objectSetting = objectSettingClient.getByObjectAndObjectId(Object.COMPANY.getId(), companyId);
//        if (objectSetting != null) {
//            return objectSetting.getCustomDomainName();
//        }
//        CompanyDTO company = companyClient.getCompanyByCompanyId(companyId).getData();
//        if (company == null) {
//            return StringUtils.EMPTY;
//        }
//        return StringUtils.defaultIfBlank(company.getCompanyCode(), StringUtils.EMPTY);
//    }

    private User createProvisionalRegisterUser(CustomerRegisterRequest request) {
        String activationCode = UUID.randomUUID().toString();

        User user = new User();

        user.setEmail(request.getEmail());
        user.setRole(this.getCustomerRole());
        user.setStatus(UserStatus.UNACTIVE.value());
        user.setActivationCode(activationCode);

        CustomerInformation customerInformation = new CustomerInformation();
        customerInformation.setObject(request.getObject());
        customerInformation.setObjectId(request.getObjectId());
        customerInformation.setUser(user);

        user.setCustomerInfomation(customerInformation);
        return repository.save(user);
    }

    private User updateProvisionalRegisterUser(CustomerRegisterRequest request) {
        String activationCode = UUID.randomUUID().toString();

        User user = this.getCustomerUserByEmail(request.getEmail());
        user.setActivationCode(activationCode);

        user.getCustomerInfomation().setObject(request.getObject());
        user.getCustomerInfomation().setObjectId(request.getObjectId());

        return repository.save(user);
    }

    private boolean isExpiredToken(LocalDateTime dateCreateToken, int numberOfDayExpired) {
        return dateCreateToken.plusDays(numberOfDayExpired)
                .isBefore(LocalDateTime.now());
    }

    private void removeForgotPasswordToken(PasswordResetDTO passwordReset) {
        if (passwordReset != null) {
            passwordReset.setDeletedAt(LocalDateTime.now());
            passwordResetService.save(passwordReset, PasswordResetDTO.class);
        }
    }

    private boolean isForgotPasswordTokenBelongsToCustomer(PasswordResetDTO passwordReset) {
        Long userId = passwordReset.getMemberId();
        Optional<User> optUser = repository.getById(userId);
        if (optUser.isPresent()) {
            String roleName = optUser.get().getRole().getRoleName();
            if (UserRole.CUSTOMER.value().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

}
