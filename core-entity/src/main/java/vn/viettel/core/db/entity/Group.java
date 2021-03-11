package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "GROUPS")
public class Group extends BaseEntity{
    @Column(name = "NAME")
    private String name;
}
