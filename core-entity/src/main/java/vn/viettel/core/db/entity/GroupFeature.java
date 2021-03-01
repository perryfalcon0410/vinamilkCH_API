package vn.viettel.core.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "group_features")
public class GroupFeature extends BaseEntity {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "charged_at")
    private LocalDateTime chargedAt;
    
	public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDateTime getChargedAt() {
		return chargedAt;
	}

	public void setChargedAt(LocalDateTime chargedAt) {
		this.chargedAt = chargedAt;
	}

}
