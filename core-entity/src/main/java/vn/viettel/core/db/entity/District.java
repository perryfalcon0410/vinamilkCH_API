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
@Table(name = "DISTRICTS")
public class District extends BaseEntity {
    @Column(name = "NAME")
    private String name;

    @Column(name = "PROVINCE_ID", nullable = false)
    private Long provinceId;

}
