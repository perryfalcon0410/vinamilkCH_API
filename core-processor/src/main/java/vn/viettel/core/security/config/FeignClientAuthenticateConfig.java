package vn.viettel.core.security.config;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.viettel.core.security.interceptor.Base64Decode;

@Configuration
public class FeignClientAuthenticateConfig {

    private static final int CONNECT_TIMEOUT_MS = 20000;
    private static final int READ_TIMEOUT_MS = 20000;

    @Value("${feign.secret-key}")
    private String secretKey;

    @Value("${security.encode-response}")
    private boolean enableEncode;

//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return (RequestTemplate requestTemplate) -> {
//            String token = AuthorizationType.FEIGN_AUTH + " " + secretKey;
//            requestTemplate.header(HttpHeaders.AUTHORIZATION, token);
//        };
//    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate requestTemplate) ->
                requestTemplate.header(HttpHeaders.AUTHORIZATION, getBearerTokenHeader());
    }

    public static String getBearerTokenHeader() {
        if(RequestContextHolder.getRequestAttributes() == null) return null;
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
    }

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Decoder base64Decode() {
        SpringDecoder springDecoder = new SpringDecoder(messageConverters);

        Decoder decoder = this.enableEncode ? new Base64Decode(springDecoder) : new ResponseEntityDecoder(springDecoder);
        return decoder;
    }

    @Bean
    @Scope("prototype")
    @Primary
    public Request.Options options() {
        return new Request.Options(CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
    }

}
