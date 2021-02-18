package vn.viettel.authorization.service;

import io.jsonwebtoken.Claims;

public interface TokenGenerateService {
    String createToken(Claims claims);

    void saveToken(String token);

    boolean getBlackListToken(String token);
}
