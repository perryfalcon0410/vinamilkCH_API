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

    private String shopSlug;

    private String area;

    private String phone;

    private String mobiphone;

    private String fax;

    private String address;

    private String email;

    private Integer status;

    private String lat;

    private String lng;

    private Integer shopType;


}
