package vn.viettel.core.security;

import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.service.feign.UserClient;
import vn.viettel.core.util.StreamUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtTokenValidate {

    final String PUB_START = "-----BEGIN PUBLIC KEY-----";
    final String PUB_END = "-----END PUBLIC KEY-----";

    @Autowired
    UserClient userClient;

    public JwtTokenValidate() {
    }

    public boolean checkExpiredToken(String token) {
        if (StringUtils.isNotBlank(token)) {
            try {
                Claims claims = Jwts.parser().setSigningKey(this.getPublicKey()).parseClaimsJws(token).getBody();

                if (isExpired(claims.getExpiration()) /*|| userClient.getBlackListToken(token)*/) {
                    return true;
                } else {
                    return false;
                }
            } catch (ExpiredJwtException e) {
                return true;
            }
        }
        return true;
    }

    public JwtTokenBody getJwtBodyByToken(String token) {
        JwtTokenBody jwtTokenBody = null;

        Claims claims = getClaimsByToken(token);
        if (claims != null) {
            jwtTokenBody = new JwtTokenBody();
            String role = (String) claims.get(TokenBodyKeyName.ROLE);
            Number userId = (Integer) claims.get(TokenBodyKeyName.USER_ID);
            // IN CASE OF LOGIN FROM ANOTHER DIMESION !
            Long companyId = null;
            if (claims.get(TokenBodyKeyName.COMPANY_ID)!=null) {
                companyId = ((Integer) claims.get(TokenBodyKeyName.COMPANY_ID)).longValue();
            }

            java.lang.Object o = claims.get(TokenBodyKeyName.OBJECT);
            Object object = null;
            if (o != null) {
                object = Object.getByName((String) o);
            }

            Number objectId = (Integer) claims.get(TokenBodyKeyName.OBJECT_ID);

            jwtTokenBody.setRole(role);
            if (userId != null) {
                jwtTokenBody.setUserId(userId.longValue());
            }
            if (companyId != null) {
                jwtTokenBody.setCompanyId(companyId);
            }
            if (object != null) {
                jwtTokenBody.setObject(object);
            }
            if (objectId != null) {
                jwtTokenBody.setObjectId(objectId.longValue());
            }
        }
        return jwtTokenBody;
    }

    public Claims getClaimsByToken(String token) {
        PublicKey publicKey = this.getPublicKey();
        Claims claims = null;
        try {
            if (StringUtils.isNotBlank(token)) {
                claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();

                if (isExpired(claims.getExpiration())) {
                    claims = null;
                }
            }
        } catch (JwtException e) {
        }
        return claims;
    }

    private boolean isExpired(Date date) {
        return date.before(new Date());
    }

    private PublicKey getPublicKey() {
        PublicKey publicKey = null;
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
            Resource resource = context.getResource("classpath:/machikatsu.pub");
            InputStream is = resource.getInputStream();
            byte[] bytes = StreamUtils.readStream(is);

            String base64Key = new String(bytes)
                .replace(PUB_START, "")
                .replace(PUB_END, "");

            byte[] binaryKey = Base64.getMimeDecoder()
                .decode(base64Key);

            X509EncodedKeySpec ks = new X509EncodedKeySpec(binaryKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(ks);

            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

}
