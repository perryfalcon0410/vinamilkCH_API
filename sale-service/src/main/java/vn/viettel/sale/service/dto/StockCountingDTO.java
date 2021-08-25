package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StockCountingDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã kiểm kê")
    private String stockCountingCode;
    @ApiModelProperty(notes = "Ngày kiểm kê")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime countingDate;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id kho hàng")
    private Long wareHouseTypeId;
    @ApiModelProperty(notes = "Id kho hàng")
    private String wareHouseTypeName;


    public StockCountingDTO(Long id, String stockCountingCode, LocalDateTime countingDate,
                            Long shopId, Long wareHouseTypeId, String wareHouseTypeName, String createdBy, String updatedBy) {
        this.setId(id);
        this.setCreatedBy(createdBy);
        this.setUpdatedBy(updatedBy);
        this.stockCountingCode = stockCountingCode;
        this.countingDate = countingDate;
        this.shopId = shopId;
        this.wareHouseTypeId = wareHouseTypeId;
        this.wareHouseTypeName = wareHouseTypeName;
    }
}
