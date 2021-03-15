package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "ADDRESS")
public class Address extends BaseEntity{
    @Column(name = "NAME")
    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "WARD_ID")
    private Ward ward;

    public Address() {}

    public Address(String name) {
        this.name = name;
    }

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
