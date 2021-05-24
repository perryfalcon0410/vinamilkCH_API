package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCountingDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã kho kiểm kê")
    private String stockCountingCode;
    @ApiModelProperty(notes = "Ngày kiểm kê")
    private Date countingDate;
    @ApiModelProperty(notes = "ID của shop")
    private Long shopId;
    @ApiModelProperty(notes = "Mã loại kho")
    private Long wareHouseTypeId;
    @ApiModelProperty(notes = "Người tạo")
    private String createUser;
    @ApiModelProperty(notes = "Người cập nhật")
    private String updateUser;
}
