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
@Table(name = "AREAS")
public class Area extends BaseEntity {
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "COUNTRY_ID", nullable = false)
    private Long countryId;

}
