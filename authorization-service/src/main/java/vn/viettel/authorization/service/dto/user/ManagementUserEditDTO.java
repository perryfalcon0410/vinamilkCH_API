package vn.viettel.authorization.service.dto.user;

import vn.viettel.authorization.service.dto.ManagementPrivilegeDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

public class ManagementUserEditDTO extends BaseDTO {
    private String email;
    private String name;
    private String password;
    private String status;
    private Long companyId;
    List<ManagementPrivilegeDTO> privileges;

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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<ManagementPrivilegeDTO> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<ManagementPrivilegeDTO> privileges) {
        this.privileges = privileges;
    }
}
