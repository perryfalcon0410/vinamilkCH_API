package vn.viettel.core.security.interceptor;

import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.security.FeignTokenValidate;
import vn.viettel.core.security.JwtTokenBody;
import vn.viettel.core.security.JwtTokenValidate;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.security.context.UserContext;
import vn.viettel.core.util.AuthorizationType;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.UnAuthorizationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

@Component
public class CheckRoleInterceptor extends HandlerInterceptorAdapter {

    private static final String CLASS_NAME_PREFIX_OF_ROLE = "role";
    private static final String SEPARATOR_IN_ROLE = "_";

    @Autowired
    SecurityContexHolder securityContexHolder;

    @Autowired
    JwtTokenValidate jwtTokenValidate;

    @Autowired
    FeignTokenValidate feignTokenValidate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, java.lang.Object handler) throws Exception {
        if (HandlerMethod.class.isAssignableFrom(handler.getClass())) {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String role = getRoleFromAuthorizationHeader(authorizationHeader);
            boolean isValid = checkValidRole((HandlerMethod) handler, role);

            if (!isValid) {
                UnAuthorization();
            }
        }
        return super.preHandle(request, response, handler);
    }

    public String getRoleFromAuthorizationHeader(String authorizationHeader) {
        String role = StringUtils.EMPTY;
        String authorizationType = getAuthorizationType(authorizationHeader);
        if (authorizationType.equals(AuthorizationType.BEARER_TOKEN)) {
            role = getRoleByBearerTokenAndSetUserContext(authorizationHeader);
        } else if (authorizationType.equals(AuthorizationType.FEIGN_AUTH)) {
            role = getRoleByFeignToken(authorizationHeader);
        }
        return role;
    }

    private String getAuthorizationType(String authorizationHeader) {
        String authorizationType = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(authorizationHeader)) {
            if (authorizationHeader.trim().startsWith(AuthorizationType.BEARER_TOKEN)) {
                authorizationType = AuthorizationType.BEARER_TOKEN;
            } else if (authorizationHeader.trim().startsWith(AuthorizationType.FEIGN_AUTH)) {
                authorizationType = AuthorizationType.FEIGN_AUTH;
            }
        }
        return authorizationType;
    }

    private String getRoleByBearerTokenAndSetUserContext(String authorizationHeader) {
        String role = StringUtils.EMPTY;
        String token = authorizationHeader.substring(AuthorizationType.BEARER_TOKEN.length()).trim();
        JwtTokenBody jwtTokenBody = jwtTokenValidate.getJwtBodyByToken(token);
        if (jwtTokenBody != null) {
            role = StringUtils.defaultIfBlank(jwtTokenBody.getRole(), StringUtils.EMPTY);
            Long userId = jwtTokenBody.getUserId();
            Object object = jwtTokenBody.getObject();
            Long objectId = jwtTokenBody.getObjectId();
            setUserContext(role, userId, object, objectId);
        }
        return role;
    }

    private void setUserContext(String role, Long userId, Object object, Long objectId) {
        UserContext context = securityContexHolder.getContext();
        if (role != null) {
            context.setRole(role);
        }
        if (userId != null) {
            context.setUserId(userId);
        }
        if (object != null) {
            context.setObject(object);
        }
        if (objectId != null) {
            context.setObjectId(objectId);
        }
        securityContexHolder.setContext(context);
    }

    private String getRoleByFeignToken(String authorizationHeader) {
        String token = authorizationHeader.substring(AuthorizationType.FEIGN_AUTH.length()).trim();
        if (feignTokenValidate.isValidToken(token)) {
            return UserRole.FEIGN.value();
        } else {
            return StringUtils.EMPTY;
        }
    }

    private boolean checkValidRole(HandlerMethod handler, String role) {
        boolean valid = true;
        if (hasCheckRoleInClass(handler)) {
            valid = checkValidRoleInClass(handler, role);
        } else if (hasCheckRoleInMethod(handler)) {
            valid = checkValidRoleInMethod(handler, role);
        }
        return valid;
    }

    private boolean checkValidRoleInClass(HandlerMethod handler, String role) {
        for (Annotation annotation : handler.getMethod().getDeclaringClass().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (checkAnnotationNameMatchRole(annotationName, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkValidRoleInMethod(HandlerMethod handler, String role) {
        for (Annotation annotation : handler.getMethod().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (checkAnnotationNameMatchRole(annotationName, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkAnnotationNameMatchRole(String annotationName, String role) {
        if (annotationName.startsWith(CLASS_NAME_PREFIX_OF_ROLE)) {
            String annotationRole = annotationName.substring(CLASS_NAME_PREFIX_OF_ROLE.length());
            role = StringUtils.join(role.split(SEPARATOR_IN_ROLE)[1]).toLowerCase();
            if (annotationRole.equals(role)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCheckRoleInClass(HandlerMethod handler) {
        for (Annotation annotation : handler.getMethod().getDeclaringClass().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (annotationName.startsWith(CLASS_NAME_PREFIX_OF_ROLE)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCheckRoleInMethod(HandlerMethod handler) {
        for (Annotation annotation : handler.getMethod().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (annotationName.startsWith(CLASS_NAME_PREFIX_OF_ROLE)) {
                return true;
            }
        }
        return false;
    }

    private void UnAuthorization() throws UnAuthorizationException {
        throw new UnAuthorizationException(ResponseMessage.NOT_AUTHORIZED.statusCodeValue());
    }
}
