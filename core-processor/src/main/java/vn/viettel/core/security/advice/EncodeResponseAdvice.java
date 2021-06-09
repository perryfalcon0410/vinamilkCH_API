package vn.viettel.core.security.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.util.Base64;

@ControllerAdvice
public class EncodeResponseAdvice implements ResponseBodyAdvice {

    @Value("${security.encode-response}")
    private boolean enableEncode;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return this.enableEncode;
    }

    @Override public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String bodyString = mapper.writeValueAsString(body);
            String bodyEncode = Base64.getEncoder().encodeToString(bodyString.getBytes());
            // JsonNode json = mapper.readTree(bodyString);
            return bodyEncode;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "has error when encode data";
        } catch (IOException e) {
            e.printStackTrace();
            return "has error when encode data";
        }
    }
}
