package vn.viettel.core.security.config;

import vn.viettel.core.security.FeignTokenValidate;
import vn.viettel.core.security.JwtTokenValidate;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.security.interceptor.CheckRoleInterceptor;
import vn.viettel.core.security.interceptor.FeatureOnOffInterceptor;
import vn.viettel.core.security.interceptor.TokenExpiredInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableFeignClients(basePackages = {"vn.viettel.core.service.feign"})
public class ApiSecurityConfig implements WebMvcConfigurer {

    @Value("${feign.secret-key}")
    private String feignSecretKey;

    @Bean
    public JwtTokenValidate jwtTokenValidate() {
        return new JwtTokenValidate();
    }

    @Bean
    public FeignTokenValidate feignTokenValidate() {
        return new FeignTokenValidate(feignSecretKey);
    }

    @Bean
    public CheckRoleInterceptor checkRoleInterceptor() {
        return new CheckRoleInterceptor();
    }

    @Bean
    public FeatureOnOffInterceptor checkFeatureIsAvailable() {
        return new FeatureOnOffInterceptor();
    }

    @Bean
    public TokenExpiredInterceptor checkTokenExpiredInterceptor() {
        return new TokenExpiredInterceptor();
    }

    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Bean
    public SecurityContexHolder securityContexHolder() {
        return new SecurityContexHolder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkTokenExpiredInterceptor());
        registry.addInterceptor(checkFeatureIsAvailable());
        registry.addInterceptor(checkRoleInterceptor());
    }



}
