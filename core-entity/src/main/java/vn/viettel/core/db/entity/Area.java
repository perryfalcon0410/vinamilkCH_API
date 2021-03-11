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
@AttributeOverride(name = "ID", column = @Column(name = "AREA_ID"))
public class Area extends BaseEntity {
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "COMPANY_ID")
    private Long companyId;

    @Column(name = "POSITION")
    private Long position;
}
