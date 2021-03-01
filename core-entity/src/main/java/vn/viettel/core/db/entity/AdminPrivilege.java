package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "service_admin_privileges")
@AttributeOverride(name = "id", column = @Column(name = "service_admin_privilege_id"))
public class AdminPrivilege extends BaseEntity {

    @Column(name = "privilege_name")
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