package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "service_admin_users")
@AttributeOverride(name = "id", column = @Column(name = "service_admin_user_id"))
public class AdminUser extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @Column(name = "status")
    private boolean status;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "photo_url")
    private String photoUrl;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_admin_privilege_id", referencedColumnName = "service_admin_privilege_id")
    @JsonManagedReference
    private AdminPrivilege privilege;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public AdminPrivilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(AdminPrivilege privilege) {
        this.privilege = privilege;
    }
}
