package vn.viettel.core.db.entity.role;

import vn.viettel.core.util.status.Validatable;

public enum UserRole implements Validatable {

    ADMIN("ADMIN"),
    FEIGN("FEIGN");

    private String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String value() {
        return roleName;
    }

    @Override
    public String validateValue() {
        return String.valueOf(this.roleName);
    }

}
