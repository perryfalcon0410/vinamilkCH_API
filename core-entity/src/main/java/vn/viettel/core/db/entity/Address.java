package vn.viettel.core.db.entity;

import javax.persistence.*;

@Entity
@Table(name = "address")
public class Address extends BaseEntity{
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    public Address() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }
}
