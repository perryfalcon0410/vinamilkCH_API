package vn.viettel.core.security.advice;

import vn.viettel.core.CommonConstants;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.feign.UserClient;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestAdvice implements ResponseBodyAdvice {

    @Autowired
    UserClient userClient;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest requestConverted = ((ServletServerHttpRequest) request).getServletRequest();

        if (requestConverted.getAttribute(CommonConstants.REQUEST_SECRET_MARK_AS_OUTSIDE_REQUEST)!=null) {
            boolean isOutsideClient =
                    (boolean) requestConverted.getAttribute(CommonConstants.REQUEST_SECRET_MARK_AS_OUTSIDE_REQUEST);
            if (isOutsideClient) {
                Claims claims = (Claims) requestConverted.getAttribute(CommonConstants.REQUEST_SECRET_MARK_AS_CLAIM_OUTSIDE_REQUEST);
                // 1. invalidate the current token
                // this approach eliminating the stateless of jwt token!
                // but since we hae to do this in order to maintain the elizabeth requirement
                // there is nothing we can do
                // we will delete all the old token in database though.
                String token = (String) requestConverted.getAttribute(CommonConstants.REQUEST_SECRET_MARK_AS_OLD_TOKEN);

//                userClient.storeToken(token);

                // 2. generate new token
//                String newToken = userClient.generateContinueToken(claims);

                // 3. Check if the body is a response type
                try {
                    Response bodyExtend = (Response) body;
//                    bodyExtend.setToken(newToken);
                    body = bodyExtend;
                } catch (Exception e) {
                    // skip the token expend
                }
            }
        }
        return body;
    }
}
