package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "privileges_users")
@AttributeOverride(name = "id", column = @Column(name = "privilege_user_id"))
public class PrivilegesUsers extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "management_user_id", referencedColumnName = "management_user_id")
    @JsonManagedReference
    private ManagementUsers managementUsers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "management_privilege_id", referencedColumnName = "management_privilege_id")
    @JsonManagedReference
    private ManagementPrivilege managementPrivilege;

    public ManagementUsers getManagementUsers() {
        return managementUsers;
    }

    public void setManagementUsers(ManagementUsers managementUsers) {
        this.managementUsers = managementUsers;
    }

    public ManagementPrivilege getManagementPrivilege() {
        return managementPrivilege;
    }

    public void setManagementPrivilege(ManagementPrivilege managementPrivilege) {
        this.managementPrivilege = managementPrivilege;
    }
}


