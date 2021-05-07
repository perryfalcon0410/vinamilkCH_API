package vn.viettel.core.security.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.FeatureNotAvailableException;
import vn.viettel.core.security.JwtTokenValidate;
import vn.viettel.core.util.AuthorizationType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

@Component
public class FeatureOnOffInterceptor extends HandlerInterceptorAdapter {

    private static final String FEATURE_IDENTIFY = "OnFeature";

    @Autowired
    JwtTokenValidate jwtTokenValidate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HandlerMethod.class.isAssignableFrom(handler.getClass())) {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean isFeignClientCallOrByPass = this.isFeignClientCallOrByPass(authorizationHeader);
            boolean isMarkAsFeature = this.isAnnotationAtClass((HandlerMethod) handler)
                    || this.isAnnotationAtMethod((HandlerMethod) handler);
            if (!isFeignClientCallOrByPass && isMarkAsFeature) {
                boolean isFeatureAvailable = true;

                if (!isFeatureAvailable) {
                    throw new FeatureNotAvailableException(ResponseMessage.COMPANY_FEATURE_NOT_AVAILABLE);
                }
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

    private boolean isAnnotationAtClass(HandlerMethod handler) {
        for (Annotation annotation : handler.getMethod().getDeclaringClass().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (annotationName.contentEquals(FEATURE_IDENTIFY.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnnotationAtMethod(HandlerMethod handler) {
        for (Annotation annotation : handler.getMethod().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (annotationName.contentEquals(FEATURE_IDENTIFY.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
