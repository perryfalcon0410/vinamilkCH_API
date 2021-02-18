package vn.viettel.authorization.service.dto.user;

import vn.viettel.authorization.service.dto.RoleDTO;
import vn.viettel.core.service.dto.BaseDTO;

public class UserIndexDTO extends BaseDTO {
    private String email;
    private String name;
    private String phone;
    private RoleDTO role;
    private String distributor;
    private String status;

    public UserIndexDTO() {
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }
}
