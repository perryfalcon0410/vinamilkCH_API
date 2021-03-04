package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;


@Getter
@Setter
@NoArgsConstructor
public class WareHouseDTO extends BaseDTO{

    private Long shop_id;
    private Long customer_id;
    private Long stocktotalId;

    private String area;
    private String warehouse_name;
    private String phone;
    private String mobiphone;
    private String fax;
    private String address;
    private Integer status;
    private Integer warehouse_type;
}
