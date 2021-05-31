package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptUpdateRequest extends BaseRequest {
    @ApiModelProperty(notes = "Loại nhập")
    private Integer type;
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    @ApiModelProperty(notes = "Số PO")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String poCoNumber;
    @ApiModelProperty(notes = "Số hóa đơn")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String redInvoiceNo;
    @ApiModelProperty(notes = "Ngày hóa đơn")
    private Date orderDate;
    @ApiModelProperty(notes = "Số nội bộ")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String internalNumber;
    private List<@Valid ReceiptCreateDetailRequest> lstUpdate;
}
