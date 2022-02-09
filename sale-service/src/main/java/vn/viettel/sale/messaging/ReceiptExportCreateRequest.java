package vn.viettel.sale.messaging;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptExportCreateRequest extends BaseRequest {
    @ApiModelProperty("Loại xuất")
    private Integer importType;
    @ApiModelProperty("Id phiếu nhập")
    private Long receiptImportId;
    private List<ReceiptCreateDetailRequest> litQuantityRemain;
    @ApiModelProperty("Kiểm tra có trả hết phiếu nhập")
    private Boolean isRemainAll;
    ////////////////////////////////////////////
    @ApiModelProperty("Mã xuất")
    private String transCode;
    @ApiModelProperty("Ngày xuất")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
    @ApiModelProperty("Loại xuất")
    private Integer type;
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    @ApiModelProperty("Số hóa đơn")
    private String redInvoiceNo;
    @ApiModelProperty("Số nội bộ")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String internalNumber;
    @ApiModelProperty("Số PO")
    @MaxTextLength(length = 50, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String poNumber;
    @ApiModelProperty("Ngày hóa đơn")
    private LocalDateTime orderDate;
    @ApiModelProperty("Ghi chú")
    @MaxTextLength(length = 250, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String note;
}
