package vn.viettel.authorization.config;

import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.core.security.config.ApiSecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends ApiSecurityConfig {

    @Value("${jwt.token.validity-seconds}")
    private int tokenValidationSeconds;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenCreate jwtTokenCreate() {
        return new JwtTokenCreate(tokenValidationSeconds);
    }
}
