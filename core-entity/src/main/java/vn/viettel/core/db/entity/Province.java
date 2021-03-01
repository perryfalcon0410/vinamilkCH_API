package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "provinces")
public class Province extends BaseEntity{
    @Column(name = "name")
    private String name;
    @Column(name = "area_id")
    private long areaId;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "province")
    private List<District> districts;

}
