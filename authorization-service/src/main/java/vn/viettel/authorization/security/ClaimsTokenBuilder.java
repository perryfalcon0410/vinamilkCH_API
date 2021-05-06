package vn.viettel.authorization.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import vn.viettel.core.security.TokenBodyKeyName;
import vn.viettel.core.service.dto.DataPermissionDTO;

import java.util.List;

public class ClaimsTokenBuilder {

    private static Claims claims;

    private ClaimsTokenBuilder(String role) {
        claims = Jwts.claims();
        claims.put(TokenBodyKeyName.ROLE, role);
    }

    public static ClaimsTokenBuilder build(String role) {
        return new ClaimsTokenBuilder(role);
    }

    public ClaimsTokenBuilder withUserId(long userId) {
        claims.put(TokenBodyKeyName.USER_ID, userId);
        return this;
    }

    public ClaimsTokenBuilder withShopId(Long shopId) {
        claims.put(TokenBodyKeyName.SHOP_ID, shopId);
        return this;
    }

    public ClaimsTokenBuilder withRoleId(Long roleId) {
        claims.put(TokenBodyKeyName.ROLE_ID, roleId);
        return this;
    }

    public ClaimsTokenBuilder withPermission(List<DataPermissionDTO> permission) {
        claims.put(TokenBodyKeyName.PERMISSION_LIST, permission);
        return this;
    }

    public Claims get() {
        return claims;
    }

}
