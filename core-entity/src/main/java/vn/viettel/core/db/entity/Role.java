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
@Table(name = "roles")
public class Role extends BaseEntity{
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;

}
