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
        return !(   request.getRequestURI().startsWith("/api/v1/users"));
    }

    @SneakyThrows
    @Override
    public Object run() {
        final RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.remove("error.status_code");

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

        if(!authClient.gateWayCheckPermissionType2(jwtTokenBody.getRoleId(), jwtTokenBody.getShopId())) {
            customizeZuulException(requestContext, ResponseMessage.USER_HAVE_NO_PRIVILEGE_ON_THIS_SHOP);
            return null;
        }

        return null;
    }

    public void customizeZuulException(RequestContext requestContext, ResponseMessage error) throws JsonProcessingException {
        Response<String> response = new Response<>();
        response.setFailure(error.statusCode(), error.statusCodeValue());
        ObjectMapper objectMapper = new ObjectMapper();
        requestContext.setResponseBody(objectMapper.writeValueAsString(response));
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setContentType("application/json; charset=utf8");
    }

    public static String getTokenFromAuthorizationHeader(String header) {
        String token = header.replace("Bearer ", "");
        return token.trim();
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









