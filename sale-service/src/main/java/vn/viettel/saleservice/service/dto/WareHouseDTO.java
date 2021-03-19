package vn.viettel.saleservice.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.ReceiptExport;
import vn.viettel.core.db.entity.ReceiptImport;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;


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
