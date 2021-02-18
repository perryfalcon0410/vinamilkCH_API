package vn.viettel.authorization.service.dto;

import vn.viettel.core.service.dto.BaseDTO;

public class RoleDTO extends BaseDTO {

    private String name;
    private String roleName;
    private byte isSuperadmin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public byte getIsSuperadmin() {
        return isSuperadmin;
    }

    public void setIsSuperadmin(byte isSuperadmin) {
        this.isSuperadmin = isSuperadmin;
    }

}
