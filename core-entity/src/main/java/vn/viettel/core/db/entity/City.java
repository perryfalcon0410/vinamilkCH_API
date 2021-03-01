package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cities")
@AttributeOverride(name = "id", column = @Column(name = "city_id"))
public class City extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "prefecture_id")
    private long prefectureId;

}
