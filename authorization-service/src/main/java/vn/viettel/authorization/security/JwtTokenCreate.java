package vn.viettel.authorization.security;

import vn.viettel.core.util.AuthorizationType;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StreamUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenCreate implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String TOKEN_PREFIX = AuthorizationType.BEARER_TOKEN;
    final String PEM_START = "-----BEGIN PRIVATE KEY-----";
    final String PEM_END = "-----END PRIVATE KEY-----";

    private int tokenValidationSeconds;

    public JwtTokenCreate(int tokenValidationSeconds) {
        this.tokenValidationSeconds = tokenValidationSeconds;
    }

    public String createToken(Claims claims) {
        PrivateKey privateKey = this.getPrivateKey();
        LocalDateTime ldtExpiredDate = LocalDateTime.now().plusSeconds(tokenValidationSeconds);
        Date expiredDate = DateUtils.parseToDate(ldtExpiredDate.toLocalDate(), ldtExpiredDate.toLocalTime());
        String token = Jwts.builder()
            .setClaims(claims)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.RS256, privateKey).compact();
        return TOKEN_PREFIX + StringUtils.SPACE + token;
    }

    private PrivateKey getPrivateKey() {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
            Resource resource = context.getResource("classpath:/machikatsu.key");
            InputStream is = resource.getInputStream();
            byte[] bytes = StreamUtils.readStream(is);

            String base64Key = new String(bytes)
                .replace(PEM_START, "")
                .replace(PEM_END, "");

            byte[] binaryKey = Base64.getMimeDecoder()
                .decode(base64Key);

            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(binaryKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(ks);
            return privateKey;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

}
