package vn.viettel.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "actions")
public class Action extends BaseEntity{
    @Column(name = "name")
    private String name;

    public Action(String name) {
        this.name = name;
    }

    public Action() {}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
