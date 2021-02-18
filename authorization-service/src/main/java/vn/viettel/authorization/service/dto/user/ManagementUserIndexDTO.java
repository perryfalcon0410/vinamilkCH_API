package vn.viettel.authorization.service.dto.user;

import vn.viettel.authorization.service.dto.ManagementPrivilegeDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

public class ManagementUserIndexDTO extends BaseDTO {
    private String email;
    private String name;
    private Boolean status;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
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
