package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin sản phẩm trong hóa đơn đỏ")
public class ProductDataDTO extends BaseDTO {

    @ApiModelProperty(notes = "ID sản phẩm")
    private Long productId;

    @ApiModelProperty(notes = "Số lượng")
    private Float quantity;

    @ApiModelProperty(notes = "Giá chưa thuế")
    private Float priceNotVat;

    @ApiModelProperty(notes = "Giá trị chưa thuế")
    private Float amountNotVat;

    @ApiModelProperty(notes = "Phần trăm thuế")
    private Integer vat;

    @ApiModelProperty(notes = "Giá trị gồm thuế")
    private Float valueAddedTax;

    @ApiModelProperty(notes = "Thuế ")
    private String groupVat;

    @ApiModelProperty(notes = "Ghi chú trong hóa đơn")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String noteRedInvoiceDetail;

}

