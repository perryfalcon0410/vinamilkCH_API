package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouse")
public class WareHouse extends BaseEntity{
    @Column(name = "shop_id")
    private Long shopId;

    @OneToMany(mappedBy = "warehouse_id")
    List<ReceiptImport> lstReceiptImport;

    @Column(name = "warehouse_name")
    private String warehouseName;

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
    private Integer warehouseType;

    public WareHouse(String warehouseName, String address) {
        this.address = address;
        this.warehouseName = warehouseName;
    }
}
