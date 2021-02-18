package vn.viettel.core.db.entity;

import javax.persistence.*;

@Entity
@Table(name = "management_privileges")
@AttributeOverride(name = "id", column = @Column(name = "management_privilege_id"))
public class ManagementPrivilege extends BaseEntity {
    @Column(name = "privilege_name", nullable = false)
    private String privilegeName;

    @Column(name = "is_super_admin")
    private boolean isSuperAdmin;

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }
}
