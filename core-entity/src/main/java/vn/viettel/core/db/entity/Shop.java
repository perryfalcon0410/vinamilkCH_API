
package vn.viettel.core.db.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SHOPS")
public class Shop extends BaseEntity {

    @Column(name = "SHOP_CODE")
    private String shopCode;

    @Column(name = "SHOP_NAME")
    private String shopName;

    @Column(name = "PARENT_SHOP_ID")
    private Long parentShopId;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "MOBILE_PHONE")
    private String mobilePhone;

    @Column(name = "SHOP_TYPE_ID")
    private Long shopTypeId;

    @Column(name = "FAX")
    private String fax;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "TAX_NUM")
    private String taxNum;
    @Column(name = "AREA_ID")
    private Long areaId;

    @Column(name = "SHOP_LOCATION")
    private String shopLocation;

    @Column(name = "UNDER_SHOP")
    private String underShop;

    @Column(name = "STATUS")
    private Integer status;





}
