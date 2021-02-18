package vn.viettel.authorization.service.dto.user;

import vn.viettel.authorization.service.dto.AdminPrivilegeDTO;
import vn.viettel.core.service.dto.BaseDTO;

public class AdminUserEditDTO extends BaseDTO {
    private String email;
    private String name;
    private String password;
    private String status;
    private AdminPrivilegeDTO adminPrivilege;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AdminPrivilegeDTO getAdminPrivilege() {
        return adminPrivilege;
    }

    public void setAdminPrivilege(AdminPrivilegeDTO adminPrivilege) {
        this.adminPrivilege = adminPrivilege;
    }
}
