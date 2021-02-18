package vn.viettel.core.db.entity.role;

import vn.viettel.core.db.entity.status.Validatable;

public enum UserRole implements Validatable {

    ADMIN("ADMIN"),
    SHOP_OWNER("SHOP_OWNER"),
    SHOP_MANAGER("SHOP_MANAGER"),
    SHOP_EMPLOYEE("SHOP_EMPLOYEE"),
    GROUP_MANAGER("GROUP_MANAGER"),
    GROUP_EMPLOYEE("GROUP_EMPLOYEE"),
    CUSTOMER("CUSTOMER"),
    DISTRIBUTOR("DISTRIBUTOR"),
    FEIGN("FEIGN"),
    MANAGEMENT("MANAGEMENT"),
    MEMBER("MEMBER"),
    RECEPTION("RECEPTION");

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
