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
import vn.viettel.core.service.dto.DataPermissionDTO;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.gateway.security.JwtTokenValidate;
import vn.viettel.gateway.service.feign.AuthClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public class PreFilter extends ZuulFilter {

    @Autowired
    JwtTokenValidate jwtTokenValidate;
    @Autowired
    AuthClient authClient;

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
        String method = requestContext.getRequest().getMethod();

        ObjectMapper objectMapper = new ObjectMapper();
        boolean isValidShopId = false;

        if (formId == null) {
            customizeZuulException(requestContext, ResponseMessage.FORM_ID_CAN_NOT_BE_NULL);
            return null;
        }
        if (!method.equals("GET"))
            if (ctrlId == null) {
                customizeZuulException(requestContext, ResponseMessage.CONTROL_ID_CAN_NOT_BE_NULL);
                return null;
            }
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

        if (jwtTokenBody.getPermissions().isEmpty()) {
            customizeZuulException(requestContext, ResponseMessage.NO_PRIVILEGE_ON_ANY_SHOP);
            return null;
        }

        for (int i = 0; i < jwtTokenBody.getPermissions().size(); i++) {
            DataPermissionDTO permission = objectMapper.convertValue(jwtTokenBody.getPermissions().get(i), DataPermissionDTO.class);
            if (permission.getShopId().equals(jwtTokenBody.getShopId()))
                isValidShopId = true;
        }
        if (!isValidShopId) {
            customizeZuulException(requestContext, ResponseMessage.USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP);
            return null;
        }
        // waiting for data
        List<PermissionDTO> permissions = authClient.getUserPermission(jwtTokenBody.getRoleId());

        if (!checkUserPermission(requestContext.getRequest().getRequestURI(),
                permissions, Long.valueOf(formId), Long.valueOf(ctrlId))) {
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

    public boolean checkUserPermission(String url, List<PermissionDTO> permissionList, Long formId, Long controlId) {
        for (PermissionDTO permission : permissionList) {
            List<ControlDTO> controlList = permission.getControls();
            if (permission.getId().equals(formId)) {
                if (controlId == null) {
                    if (!checkFormControlPermission(url, permission, null))
                        return false;
                    else
                        return true;
                } else {
                    for (ControlDTO ctrl : controlList) {
                        if (ctrl.getId().equals(controlId)) {
                            if (!checkFormControlPermission(url, permission, ctrl))
                                return false;
                            else
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean checkFormControlPermission(String url, PermissionDTO form, ControlDTO control) {
        String[] apiPart = url.split("v1/");
        System.out.println(url);
        String controlCode = apiPart[1].split("/")[1];

        if (url.substring(1).equals(form.getUrl())) {
            if (control == null)
                return true;
            else {
                if (control.getControlCode().equals(controlCode))
                    return true;
            }
        }
        return false;
    }
}









