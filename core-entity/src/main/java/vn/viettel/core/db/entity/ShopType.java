package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "shop_types")
public class ShopType extends BaseEntity {

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "disp_priority", nullable = false)
    @NotNull
    private Long dispPriority;

    @Column(name = "status", nullable = false)
    @NotNull
    private Long status = (long) 0;

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
