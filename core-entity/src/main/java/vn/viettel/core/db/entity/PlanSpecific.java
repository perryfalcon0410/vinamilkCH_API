package vn.viettel.core.db.entity;

import vn.viettel.core.db.entity.status.Object;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "plan_specific")
public class PlanSpecific extends BaseEntity {

    @Column(name = "object")
    private Object object;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "plan_id")
    private Long planId;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
