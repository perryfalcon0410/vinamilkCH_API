package vn.viettel.authorization.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.member.*;
import vn.viettel.authorization.service.dto.authorization.MemberUserLoginDTO;
import vn.viettel.core.service.BaseService;
import vn.viettel.core.exception.FeatureNotAvailableException;
import vn.viettel.authorization.messaging.member.*;

public interface MemberAuthenticateService extends BaseService {
    /* LOGIN */
    Response<MemberUserLoginDTO> login(MemberLoginRequest request) throws FeatureNotAvailableException;

    /* FORGOT PASSWORD */
    Response<String> forgotPassword(MemberForgotPasswordRequest request) throws Exception;

    /* CHECK VALID RESET PASSWORD TOKEN */
    Response<String> checkValidResetPasswordToken(String token, String companySlug, String salonSlug);

    /* UPDATE FORGOT PASSWORD */
    Response<String> updateForgotPassword(MemberUpdateForgotPasswordRequest request);

    MemberUserLoginDTO memberLogin(String email, String password, Long companyId);

    /* SEND EMAIL FOR PRE REGISTRATION  */
    Response<String> sendActivationCode(MemberSendEmailRegistrationRequest request);

    /* CHECK VALID VALIDATION ACCOUNT TOKEN */
    Response<String> checkValidActivationCode(String email, String companySlug, String salonSlug, String token);

    /**
     * Register account for member
     * @param request
     * @return
     * @throws Exception
     */
    Response<MemberRegisterResponse> registerAccount(MemberRegisterRequest request);

    /**
     * Precheck registering account for member
     * @param request
     * @return
     * @throws Exception
     */
    Response<MemberRegisterResponse> precheckRegisterAccount(MemberRegisterRequest request);
}
