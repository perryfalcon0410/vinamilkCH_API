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
public class RedInvoiceDetailDTO extends  BaseDTO{
    @ApiModelProperty(notes = "Id hóa đơn đỏ")
    private Long redInvoiceId;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Ngày in hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime printDate;
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Đơn giá sau thuế")
    private Float price;
    @ApiModelProperty(notes = "Đơn giá trước thuế")
    private Float priceNotVat;
    @ApiModelProperty(notes = "Thành tiền trước thuế")
    private Float amountNotVat;
    @ApiModelProperty(notes = "Thành tiền sau thuế")
    private Float amount;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;

}
