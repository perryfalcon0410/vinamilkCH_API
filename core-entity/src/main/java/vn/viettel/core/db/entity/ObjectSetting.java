package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "object_setting")
public class ObjectSetting extends BaseEntity {

    @Column(name = "object", nullable = false)
    private Integer object;

    @Column(name = "object_id", nullable = false)
    private Long objectId;

    @Column(name = "custom_domain_name")
    private String customDomainName;

    @Column(name = "is_active")
    private Boolean isActive;

    public Integer getObject() {
        return object;
    }

    public void setObject(Integer object) {
        this.object = object;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getCustomDomainName() {
        return customDomainName;
    }

    public void setCustomDomainName(String customDomainName) {
        this.customDomainName = customDomainName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
