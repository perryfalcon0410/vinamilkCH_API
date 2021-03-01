package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reception_sub_management_users")
@AttributeOverride(name = "id", column = @Column(name = "reception_sub_management_user_id"))
public class ReceptionSubManagementUser extends BaseEntity {
    
    @Column(name = "reception_id", nullable = false)
    private Long receptionId;

    @Column(name = "management_user_id", nullable = false)
    private Long managementUserId;

    @Column(name = "reception_management_type_id", nullable = false)
    private Long receptionManagementTypeId;

    public Long getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(Long receptionId) {
        this.receptionId = receptionId;
    }

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public Long getReceptionManagementTypeId() {
        return receptionManagementTypeId;
    }

    public void setReceptionManagementTypeId(Long receptionManagementTypeId) {
        this.receptionManagementTypeId = receptionManagementTypeId;
    }
}
