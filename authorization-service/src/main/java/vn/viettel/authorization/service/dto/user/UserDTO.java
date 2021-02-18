package vn.viettel.authorization.service.dto.user;

import vn.viettel.authorization.service.dto.RoleDTO;
import vn.viettel.core.db.entity.User;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.service.dto.annotation.JsonIgnoreModifiedTimeProperties;

@JsonIgnoreModifiedTimeProperties
public class UserDTO extends BaseDTO {

    private String email;
    private String name;
    private String photo;
    private String password;
    private String status;
    private RoleDTO role;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.photo = user.getPhoto();
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

}
