package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateRequest {
    @ApiModelProperty(notes = "Id PoConfirm")
    private Long poId;
    @ApiModelProperty(notes = "Loại nhập hàng")
    private Integer importType;
    ////////////////////////////////////////////
    @ApiModelProperty(notes = "Mã nhập hàng")
    private String transCode;
    @ApiModelProperty(notes = "Ngày nhập hàng")
    private Date transDate;
    @ApiModelProperty(notes = "Loại phiếu")
    private Integer type;
    @ApiModelProperty(notes = "Số hóa đơn")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String redInvoiceNo;
    @ApiModelProperty(notes = "Số nội bộ")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String internalNumber;
    @ApiModelProperty(notes = "Số PO")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String poCoNumber;
    @ApiModelProperty(notes = "Ngày hóa đơn")
    private Date orderDate;
    @ApiModelProperty(notes = "Ghi chú")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String note;
    private List<@Valid ReceiptCreateDetailRequest> lst;

}
