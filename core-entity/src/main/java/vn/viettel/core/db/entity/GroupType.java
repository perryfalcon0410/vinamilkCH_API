package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "group_types")
public class GroupType extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "disp_priority", nullable = false)
    private Long dispPriority;

    @Column(name = "status", nullable = false)
    private Long status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDispPriority() {
        return dispPriority;
    }

    public void setDispPriority(Long dispPriority) {
        this.dispPriority = dispPriority;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
