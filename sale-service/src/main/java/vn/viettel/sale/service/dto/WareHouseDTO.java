package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class WareHouseDTO extends BaseDTO{
    private Long shopId;
    private String warehouseCode;
    private String warehouseName;
    private Long customerId;
    private long stocktotalId;
    private Long areaId;
    private Long fullAddressId;
    private String phone;
    private String mobilePhone;
    private Long warehouseTypeId;
    private Integer status;
}
