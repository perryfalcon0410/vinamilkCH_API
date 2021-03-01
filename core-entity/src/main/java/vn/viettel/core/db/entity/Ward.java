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
@Table(name = "wards")
public class Ward extends BaseEntity{
    private String name;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ward")
    private List<Address> addressList;

}
