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
@Table(name = "PROVINCES")
public class Province extends BaseEntity{
    @Column(name = "NAME")
    private String name;
    @Column(name = "AREA_ID")
    private long areaId;

    @ManyToOne
    @JoinColumn(name = "COUNTRY_ID")
    private Country country;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "province")
    private List<District> districts;

}
