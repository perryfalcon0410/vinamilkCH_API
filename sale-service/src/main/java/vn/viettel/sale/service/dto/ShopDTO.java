package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopDTO extends BaseDTO{


    private String shopCode;


    private String shopName;


    private Long parentShopId;


    private String phone;


    private String mobilePhone;
    private Long shopTypeId;
    private String fax;
    private String email;
    private String address;
    private String taxNum;
    private Long areaId;
    private String shopLocation;
    private String underShop;
    private Integer status;

}
