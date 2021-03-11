package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ROLES")
public class Role extends BaseEntity{
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;

}
