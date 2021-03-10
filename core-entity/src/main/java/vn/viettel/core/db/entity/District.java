package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "districts")
public class District extends BaseEntity{
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "district")
    private List<Ward> wards;

}
