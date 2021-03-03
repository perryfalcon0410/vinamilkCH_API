package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouse")
public class WareHouse extends BaseEntity{
    @Column(name = "shop_id")
    private Long shopId;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wareHouse")
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

    @Column(name = "warehouse_type")
    private Integer warehouseType;

    @Column(name = "status")
    private Integer status;



    public WareHouse(String warehouseName, String address) {
        this.address = address;
        this.warehouseName = warehouseName;
    }
}
