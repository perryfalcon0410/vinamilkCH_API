package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AttributeOverride(name = "id", column = @Column(name = "password_reset_id"))
@Table(name = "password_resets")
public class PasswordReset extends BaseEntity {

    @Column(name = "admin_user_id")
    private Long adminUserId;

    @Column(name = "management_user_id")
    private Long managementUserId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "token")
    private String token;

	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public Long getManagementUserId() {
        return managementUserId;
    }

    public void setManagementUserId(Long managementUserId) {
        this.managementUserId = managementUserId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
