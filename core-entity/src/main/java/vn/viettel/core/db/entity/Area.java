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
@Table(name = "areas")
@AttributeOverride(name = "id", column = @Column(name = "area_id"))
public class Area extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "position")
    private Long position;
}
