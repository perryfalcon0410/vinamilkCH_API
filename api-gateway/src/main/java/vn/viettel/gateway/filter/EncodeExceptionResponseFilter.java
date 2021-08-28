package vn.viettel.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Value;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.gateway.messaging.ResponseError;

import java.util.Base64;

public class EncodeExceptionResponseFilter extends ZuulFilter {

    private static final String FILTER_TYPE = "error";
    private static final String THROWABLE_KEY = "throwable";
    private static final int FILTER_ORDER = -1;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${security.encode-response}")
    private boolean enableEncode;

    @Override
    public String filterType() {
        return FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER; // Needs to run before SendErrorFilter which has filterOrder == 0
    }

    @Override
    public boolean shouldFilter() {
        return enableEncode;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        Object throwable = context.get(THROWABLE_KEY);

        if (throwable != null && throwable instanceof ZuulException) {
            ZuulException zuulException = (ZuulException) throwable;
            if (zuulException.errorCause.equals("route:RibbonRoutingFilter")) {
                context.remove(THROWABLE_KEY);
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                try {
                    ResponseError response = new ResponseError(zuulException.nStatusCode, "Internal Server Error", "GENERAL");
                    String bodyString = mapper.writeValueAsString(response);
                    String bodyBase64 = Base64.getEncoder().encodeToString(bodyString.getBytes());

                    StringBuilder bodyEncode = new StringBuilder();
                    bodyEncode.append('"')
                        .append(bodyBase64)
                        .append('"');
                    context.setResponseBody(bodyEncode.toString());
                    context.getResponse().setContentType("application/json;charset=UTF-8");
                } catch (JsonProcessingException e) {
                    LogFile.logToFile(appName, null, LogLevel.ERROR, null, "has error when encode data " + e.getMessage());
                }
            }
        }
        return null;
    }
}
