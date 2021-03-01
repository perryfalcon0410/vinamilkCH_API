package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "prefectures")
@AttributeOverride(name = "id", column = @Column(name = "prefecture_id"))
public class Prefecture extends BaseEntity {
    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    @Column(name = "region_id")
    private long regionId;

    public void setName(String name) {
        this.name = name;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }
}
