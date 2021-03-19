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
public class    WareHouse extends BaseEntity{
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "WARE_HOUSE_CODE")
    private String warehouseCode;
    @Column(name = "WARE_HOUSE_NAME")
    private String warehouseName;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @Column(name = "STOCK_TOTAL_ID")
    private long stocktotalId;

    @Column(name = "AREA_ID")
    private Long areaId;
    @Column(name = "FULL_ADDRESS_ID")
    private Long fullAddressId;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;

    @Column(name = "WARE_HOUSE_TYPE_ID")
    private Long warehouseTypeId;

    @Column(name = "STATUS")
    private Integer status;

}
