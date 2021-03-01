package vn.viettel.core.db.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "channel")
@AttributeOverride(name = "id", column = @Column(name = "channel_id"))
public class Channel extends BaseEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private Long type;

    @Column(name = "parent")
    private Long parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }
}
