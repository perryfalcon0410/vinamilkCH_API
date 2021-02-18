package vn.viettel.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import vn.viettel.gateway.messaging.ResponseError;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;

public class EncodeExceptionResponseFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String FILTER_TYPE = "error";
    private static final String THROWABLE_KEY = "throwable";
    private static final int FILTER_ORDER = -1;

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
                    e.printStackTrace();
                    logger.info("has error when encode data");
                }
            }
        }
        return null;
    }
}
