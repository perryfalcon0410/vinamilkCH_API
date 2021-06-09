package vn.viettel.core.security.interceptor;

import vn.viettel.core.security.JwtTokenValidate;
import vn.viettel.core.util.AuthorizationType;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenExpiredInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    JwtTokenValidate jwtTokenValidate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HandlerMethod.class.isAssignableFrom(handler.getClass())) {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean isFeignClientCallOrByPass = this.isFeignClientCallOrByPass(authorizationHeader);
            if (!isFeignClientCallOrByPass) {
                String token = authorizationHeader.substring(AuthorizationType.BEARER_TOKEN.length()).trim();
                boolean isExpired = jwtTokenValidate.checkExpiredToken(token);
                if (isExpired) {
                    throw new TokenExpiredException(ResponseMessage.SESSION_EXPIRED);
                }
                Claims claims = jwtTokenValidate.getClaimsByToken(token);
                request.setAttribute(Constants.REQUEST_SECRET_MARK_AS_OUTSIDE_REQUEST, true);
                request.setAttribute(Constants.REQUEST_SECRET_MARK_AS_CLAIM_OUTSIDE_REQUEST, claims);
                request.setAttribute(Constants.REQUEST_SECRET_MARK_AS_OLD_TOKEN, token);
            }
        }
        return super.preHandle(request, response, handler);
    }

    private boolean isFeignClientCallOrByPass(String authorizationHeader) {
        if (StringUtils.isEmpty(authorizationHeader)) {
            return true;
        } else if (authorizationHeader.trim().startsWith(AuthorizationType.BEARER_TOKEN)) {
            return false;
        } else {
            return true;
        }
    }
}
