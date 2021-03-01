package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "companies")
public class Company extends BaseEntity{
    private String name;
    private String address;

    public Company(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
