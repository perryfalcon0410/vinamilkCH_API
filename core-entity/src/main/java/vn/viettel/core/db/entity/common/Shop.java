package vn.viettel.core.db.entity.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @Column(name = "MOBIPHONE")
    private String mobiPhone;
    @Column(name = "SHOP_TYPE_ID")
    private Long shopTypeId;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "TAX_NUM")
    private String taxNum;
    @Column(name = "INVOICE_NUMBER_ACCOUNT")
    private String invoiceNumberAccount;
    @Column(name = "INVOICE_BANK_NAME")
    private String invoiceBankName;
    @Column(name = "FAX")
    private String fax;
    @Column(name = "AREA_ID")
    private Long areaId;
    @Column(name = "SHOP_TYPE")
    private String shopType;
    @Column(name = "SHOP_LOCATION")
    private String shopLocation;
    @Column(name = "UNDER_SHOP")
    private Integer underShop;
    @Column(name = "PRICE_TYPE")
    private Integer priceType;
    @Column(name = "STORE_CODE")
    private String storeCode;
    @Column(name = "KA_CODE")
    private String kaCode;
    @Column(name = "OLD_CODE")
    private String oldCode;
    @Column(name = "ENABLE_LOG")
    private Boolean enableLog;
    @Column(name = "STATUS")
    private Integer status;
}
