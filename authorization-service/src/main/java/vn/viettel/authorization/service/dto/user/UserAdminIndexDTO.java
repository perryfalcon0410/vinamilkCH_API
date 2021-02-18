package vn.viettel.authorization.service.dto.user;

import vn.viettel.authorization.service.dto.AdminPrivilegeDTO;
import vn.viettel.core.service.dto.BaseDTO;

public class UserAdminIndexDTO extends BaseDTO {
    private String id;
    private String email;
    private String name;
    private Boolean status;
    private AdminPrivilegeDTO adminPrivalige;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public AdminPrivilegeDTO getAdminPrivalige() {
        return adminPrivalige;
    }

    public void setAdminPrivalige(AdminPrivilegeDTO adminPrivalige) {
        this.adminPrivalige = adminPrivalige;
    }
}
