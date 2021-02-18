package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cities")
@AttributeOverride(name = "id", column = @Column(name = "city_id"))
public class City extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "prefecture_id")
    private long prefectureId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(long prefectureId) {
        this.prefectureId = prefectureId;
    }
}
