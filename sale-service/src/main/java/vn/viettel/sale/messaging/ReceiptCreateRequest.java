package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateRequest {
    @ApiModelProperty(notes = "Id PoConfirm")
    private Long poId;
    @ApiModelProperty(notes = "Id loại kho")
    private Long wareHouseTypeId;
    @ApiModelProperty(notes = "Loại nhập hàng")
    private Integer importType;
    ////////////////////////////////////////////
    @ApiModelProperty(notes = "Mã nhập hàng")
    private String transCode;
    @ApiModelProperty(notes = "Ngày nhập hàng")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "Ghi chú")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String note;
    private List<@Valid ReceiptCreateDetailRequest> lst;

}
