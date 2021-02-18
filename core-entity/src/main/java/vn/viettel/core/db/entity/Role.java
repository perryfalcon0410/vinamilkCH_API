package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(length = 255)
    private String name;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "is_superadmin")
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