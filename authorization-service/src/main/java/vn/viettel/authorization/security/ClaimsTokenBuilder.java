package vn.viettel.authorization.security;

import vn.viettel.core.db.entity.Role;
import vn.viettel.core.security.TokenBodyKeyName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.List;

public class ClaimsTokenBuilder {

    private static Claims claims;

    private ClaimsTokenBuilder(List<String> roles) {
        claims = Jwts.claims();
        claims.put(TokenBodyKeyName.ROLE, roles);
    }

    public static ClaimsTokenBuilder build(List<String> roles) {
        return new ClaimsTokenBuilder(roles);
    }

    public ClaimsTokenBuilder withUserId(long userId) {
        claims.put(TokenBodyKeyName.USER_ID, userId);
        return this;
    }

    public ClaimsTokenBuilder withObject(Object object) {
        claims.put(TokenBodyKeyName.OBJECT, object);
        return this;
    }

    public ClaimsTokenBuilder withObjectId(long objectId) {
        claims.put(TokenBodyKeyName.OBJECT_ID, objectId);
        return this;
    }

    public ClaimsTokenBuilder withIsSuperAdmin(boolean isSuperAdmin) {
        claims.put(TokenBodyKeyName.IS_SUPER_ADMIN, isSuperAdmin);
        return this;
    }

    public ClaimsTokenBuilder withPrivilegeName(String privilegeName) {
        claims.put(TokenBodyKeyName.PRIVILEGE_NAME, privilegeName);
        return this;
    }

    public ClaimsTokenBuilder withPrivilegeNames(List<String> privilegeNames) {
        claims.put(TokenBodyKeyName.PRIVILEGE_NAMES, privilegeNames);
        return this;
    }

    public ClaimsTokenBuilder withCompanyId(Long companyId) {
        claims.put(TokenBodyKeyName.COMPANY_ID, companyId);
        return this;
    }

    public Claims get() {
        return claims;
    }

}
