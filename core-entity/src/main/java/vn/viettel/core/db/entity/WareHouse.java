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
@Table(name = "WARE_HOUSES")
public class WareHouse extends BaseEntity{
    @Column(name = "SHOP_ID")
    private Long shopId;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wareHouse")
    List<ReceiptImport> lstReceiptImport;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wareHouse")
    List<ReceiptExport> lstReceiptExport;

    @Column(name = "WARE_HOUSE_NAME")
    private String warehouseName;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "STOCK_TOTAL_ID")
    private long stocktotalId;

    @Column(name = "FULL_ADDRESS_ID")
    private String fullAdressId;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;

    @Column(name = "FAX")
    private String fax;

    @Column(name = "WARE_HOUSE_TYPE")
    private Integer warehouseType;

    @Column(name = "STATUS")
    private Integer status;

}
