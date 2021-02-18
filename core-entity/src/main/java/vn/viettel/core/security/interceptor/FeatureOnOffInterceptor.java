package vn.viettel.core.security.interceptor;

import vn.viettel.core.db.entity.commonEnum.Feature;
import vn.viettel.core.dto.company.CompanyFeatureListDTO;
import vn.viettel.core.security.JwtTokenBody;
import vn.viettel.core.security.JwtTokenValidate;
import vn.viettel.core.security.anotation.Feature.OnFeature;
import vn.viettel.core.service.lazy.CompanyLazyService;
import vn.viettel.core.util.AuthorizationType;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.FeatureNotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.List;

@Component
public class FeatureOnOffInterceptor extends HandlerInterceptorAdapter {

    private static final String FEATURE_IDENTIFY = "OnFeature";

    @Autowired
    JwtTokenValidate jwtTokenValidate;

    @Autowired
    CompanyLazyService companyLazyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HandlerMethod.class.isAssignableFrom(handler.getClass())) {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean isFeignClientCallOrByPass = this.isFeignClientCallOrByPass(authorizationHeader);
            boolean isMarkAsFeature = this.isAnnotationAtClass((HandlerMethod) handler)
                    || this.isAnnotationAtMethod((HandlerMethod) handler);
            if (!isFeignClientCallOrByPass && isMarkAsFeature) {
                boolean isFeatureAvailable = this.checkFeatureAvailable((HandlerMethod) handler, authorizationHeader);

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

    private Long getCompanyIdByToken(String authorizationHeader) throws FeatureNotAvailableException {
        String token = authorizationHeader.substring(AuthorizationType.BEARER_TOKEN.length()).trim();
        JwtTokenBody jwtTokenBody = jwtTokenValidate.getJwtBodyByToken(token);
        Long companyId = null;
        if (jwtTokenBody != null) {
            companyId = jwtTokenBody.getCompanyId();
        }
        if (companyId==null) throw new FeatureNotAvailableException(ResponseMessage.COMPANY_FEATURE_EXCEPTION);
        return companyId;
    }

    private boolean checkFeatureAvailable(HandlerMethod handler, String authorizationHeader) throws FeatureNotAvailableException {
        Feature feature = this.getFeatureAtMethod(handler);
        if (feature == null) {
            Feature classFeature = this.getFeatureAtClass(handler);
            if (classFeature == null) {
                throw new FeatureNotAvailableException(ResponseMessage.COMPANY_FEATURE_EXCEPTION);
            }
            feature = classFeature;
        }
        if (feature == Feature.NO_CHECK_FEATURE) {
            return true;
        }

        Long companyId = this.getCompanyIdByToken(authorizationHeader);
        List<CompanyFeatureListDTO> featureList =
                companyLazyService.getAvailableCompanyFeatureList(companyId);

        // check if the feature is currently on
        Feature finalFeature = feature;
        CompanyFeatureListDTO companyFeature =
                featureList.stream().filter(item -> item.getName().contentEquals(finalFeature.getName())).findAny().orElse(null);
        if (companyFeature == null || companyFeature.getStatus()==null || !companyFeature.getStatus()) {
            return false;
        }
        return true;
    }

    private Feature getFeatureAtClass(HandlerMethod handler) {
        for (Annotation annotation : handler.getMethod().getDeclaringClass().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (annotationName.contentEquals(FEATURE_IDENTIFY.toLowerCase())) {
                OnFeature onFeature = handler.getMethod().getDeclaringClass().getAnnotation(OnFeature.class);
                return onFeature.feature();
            }
        }
        return null;
    }

    private Feature getFeatureAtMethod(HandlerMethod handler) {
        for (Annotation annotation : handler.getMethod().getDeclaredAnnotations()) {
            String annotationName = annotation.annotationType().getSimpleName().toLowerCase();
            if (annotationName.contentEquals(FEATURE_IDENTIFY.toLowerCase())) {
                OnFeature onFeature = handler.getMethod().getAnnotation(OnFeature.class);
                return onFeature.feature();
            }
        }
        return null;
    }
}
