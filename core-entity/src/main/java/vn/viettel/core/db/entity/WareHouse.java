package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouse")
public class WareHouse extends BaseEntity{
    @Column(name = "shop_id")
    private Long shop_id;

    @Column(name = "warehouse_name")
    private String warehouse_name;

    @Column(name = "area")
    private String area;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mobiphone")
    private String mobiphone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "address")
    private String address;

    @Column(name = "status")
    private Integer status;

    @Column(name = "warehouse_type")
    private Integer warehouse_type;

    public WareHouse(String warehouse_name, String address) {
        this.address = address;
        this.warehouse_name = warehouse_name;
    }
}
