package vn.viettel.authorization.service.dto.user;

import vn.viettel.authorization.service.dto.DistributorDTO;
import vn.viettel.authorization.service.dto.RoleDTO;
import vn.viettel.core.service.dto.BaseDTO;

public class UserEditDTO extends BaseDTO {
    private String email;
    private String name;
    private String photo;
    private String password;
    private String status;
    private RoleDTO role;
    private DistributorDTO distributor;

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    public DistributorDTO getDistributor() {
        return distributor;
    }

    public void setDistributor(DistributorDTO distributor) {
        this.distributor = distributor;
    }
}
