package vn.viettel.saleservice.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

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
