package vn.viettel.core.security;

import org.springframework.stereotype.Component;

@Component
public class FeignTokenValidate {

    private String secretKey;

    public FeignTokenValidate(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isValidToken(String token) {
        return secretKey.equals(token);
    }

}
