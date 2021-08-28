package vn.viettel.authorization.service.impl;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.authorization.entities.Token;
import vn.viettel.authorization.repository.TokenRepository;
import vn.viettel.authorization.security.JwtTokenCreate;
import vn.viettel.authorization.service.TokenGenerateService;

import javax.transaction.Transactional;

@Service
public class TokenGenerateServiceImpl implements TokenGenerateService {

    @Autowired
    JwtTokenCreate jwtTokenCreate;

    @Autowired
    TokenRepository tokenRepository;

    @Override
    public String createToken(Claims claims) {
        return jwtTokenCreate.createToken(claims);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void saveToken(String token) {
        tokenRepository.pStatefulTokenStoring(token);
    }

    @Override
    public boolean getBlackListToken(String token) {
        Token tokenObj = tokenRepository.findByToken(token);
        if (tokenObj != null) {
           return true;
        }
        return false;
    }
}
