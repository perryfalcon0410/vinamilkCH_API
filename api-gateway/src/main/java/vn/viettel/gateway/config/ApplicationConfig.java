package vn.viettel.gateway.config;

import vn.viettel.gateway.filter.EncodeExceptionResponseFilter;
import vn.viettel.gateway.filter.PreFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class ApplicationConfig {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    EncodeExceptionResponseFilter encodeExceptionResponseFilter() {
        return new EncodeExceptionResponseFilter();
    }

    @Bean
    PreFilter preFilter() {
        return new PreFilter();
    }
}
