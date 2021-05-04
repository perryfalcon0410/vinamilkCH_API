package vn.viettel.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.JwtTokenBody;
import vn.viettel.core.service.dto.ControlDTO;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.gateway.security.JwtTokenValidate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PreFilter extends ZuulFilter {

    @Autowired
    JwtTokenValidate jwtTokenValidate;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        final RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        return !(request.getRequestURI().startsWith("/api/v1/users"));
    }

    @SneakyThrows
    @Override
    public Object run() {
        final RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.remove("error.status_code");
        String formId = requestContext.getRequest().getParameter("formId");
        String ctrlId = requestContext.getRequest().getParameter("ctrlId");

        HttpServletRequest request = requestContext.getRequest();
        Optional<String> header = Optional.ofNullable(request.getHeader("Authorization"));
        String token;

        try {
            token = getTokenFromAuthorizationHeader(header.get());
        } catch (Exception e) {
            customizeZuulException(requestContext, ResponseMessage.USER_TOKEN_MUST_BE_NOT_NULL);
            return null;
        }
        if (token.equals("")) {
            customizeZuulException(requestContext, ResponseMessage.USER_TOKEN_MUST_BE_NOT_NULL);
            return null;
        }
        try {
            jwtTokenValidate.isValidSignature(token);

            if (jwtTokenValidate.checkExpiredToken(token)) {
                customizeZuulException(requestContext, ResponseMessage.SESSION_EXPIRED);
                return null;
            }
        } catch (Exception e) {
            customizeZuulException(requestContext, ResponseMessage.INVALID_TOKEN);
            return null;
        }

        JwtTokenBody jwtTokenBody = jwtTokenValidate.getJwtBodyByToken(token);
        ObjectMapper objectMapper = new ObjectMapper();

        List<PermissionDTO> permissions = new ArrayList<>();

        for (int i = 0; i < jwtTokenBody.getPermissionList().size(); i++) {
            PermissionDTO permission = objectMapper.convertValue(jwtTokenBody.getPermissionList().get(i), PermissionDTO.class);
            permissions.add(permission);
        }

        if (!checkUserPermission(permissions, Long.valueOf(formId), Long.valueOf(ctrlId))) {
            customizeZuulException(requestContext, ResponseMessage.NO_FUNCTIONAL_PERMISSION);
            return null;
        }
        return null;
    }

    public void customizeZuulException(RequestContext requestContext, ResponseMessage error) throws JsonProcessingException {
        Response<String> response = new Response<>();
        response.setFailure(error.statusCode(), error.statusCodeValue());
        ObjectMapper objectMapper = new ObjectMapper();
        requestContext.setResponseBody(objectMapper.writeValueAsString(response));
    }

    public static String getTokenFromAuthorizationHeader(String header) {
        String token = header.replace("Bearer ", "");
        return token.trim();
    }

    public boolean checkUserPermission(List<PermissionDTO> permissionList, Long formId, Long controlId) {
        boolean havePrivilege = false;

        for (PermissionDTO permission : permissionList) {
            List<ControlDTO> controlList = permission.getControls();
            if (permission.getId() == formId && controlList.stream().anyMatch(ctrl -> ctrl.getId().equals(controlId)))
                havePrivilege = true;
        }
        return havePrivilege;
    }
}









