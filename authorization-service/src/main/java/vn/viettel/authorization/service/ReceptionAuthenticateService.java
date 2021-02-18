package vn.viettel.authorization.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.authorization.UserForgotPasswordRequest;
import vn.viettel.authorization.messaging.authorization.UserLoginRequest;
import vn.viettel.authorization.messaging.authorization.UserUpdateForgotPasswordRequest;
import vn.viettel.authorization.service.dto.authorization.ReceptionUserLoginDTO;
import vn.viettel.core.service.BaseService;
import vn.viettel.core.exception.FeatureNotAvailableException;

public interface ReceptionAuthenticateService extends BaseService {
    Response<ReceptionUserLoginDTO> login(UserLoginRequest request) throws FeatureNotAvailableException;

    Response<String> forgotPassword(UserForgotPasswordRequest request) throws FeatureNotAvailableException;

    Response<String> checkValidResetPasswordToken(String token);

    Response<String> updateForgotPassword(UserUpdateForgotPasswordRequest request);

    ReceptionUserLoginDTO receptionUserLogin(String email, String password) throws FeatureNotAvailableException;
}
