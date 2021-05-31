package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

@Getter
@Setter
@AllArgsConstructor
public class StockCountingUpdateDTO {
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Số lượng package kiểm kê")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer packetQuantity;
    @ApiModelProperty(notes = "Số lượng lẻ kiểm kê")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer unitQuantity;
    @ApiModelProperty(notes = "Quy đổi")
    @MaxTextLength(length = 7, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private Integer convfact;
}
