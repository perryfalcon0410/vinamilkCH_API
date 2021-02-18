package vn.viettel.authorization.service;

import vn.viettel.authorization.messaging.authorization.UserLoginForViewPageRequest;
import vn.viettel.authorization.messaging.customer.*;
import vn.viettel.authorization.service.dto.authorization.CustomerLoginDTO;
import vn.viettel.authorization.service.dto.authorization.UserLoginDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.customer.*;

public interface CustomerAuthenticateService {

    Response<String> register(CustomerRegisterRequest request);

    Response<String> checkValidActivationCode(CustomerValidActivationCodeRequest request);

    Response<String> forgotPassword(CustomerForgotPasswordRequest request);

    Response<String> checkValidForgotPasswordToken(String token);

    Response<String> updateForgotPassword(CustomerUpdatePasswordRequest request);

    Response<CustomerLoginDTO> login(CustomerLoginRequest request);

    Response<String> changePassword(CustomerChangePasswordRequest request);

    Response<UserLoginDTO> loginForViewPage(UserLoginForViewPageRequest request);
}
