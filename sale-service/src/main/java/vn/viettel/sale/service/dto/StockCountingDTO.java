package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCountingDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã kiểm kê")
    private String stockCountingCode;
    @ApiModelProperty(notes = "Ngày kiểm kê")
    private Date countingDate;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id kho hàng")
    private Long wareHouseTypeId;
}
