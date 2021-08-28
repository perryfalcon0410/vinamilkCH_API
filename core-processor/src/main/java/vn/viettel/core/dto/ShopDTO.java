package vn.viettel.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ShopDTO {
    private Long id;
    private String shopCode;
    private String shopName;
    private Long parentShopId;
    private String phone;
    private String mobiPhone;
    private Long shopTypeId;
    private String email;
    private String address;
    private String taxNum;
    private String invoiceNumberAccount;
    private String invoiceBankName;
    private String fax;
    private Long areaId;
    private String shopType;
    private String shopLocation;
    private Integer underShop;
    private Integer priceType;
    private String storeCode;
    private String kaCode;
    private String oldCode;
    private Boolean enableLog;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //export excel
    private ShopDTO parentShop;
}
