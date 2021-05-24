package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptUpdateRequest extends BaseRequest {
    @ApiModelProperty(notes = "Loại nhập")
    private Integer type;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    @ApiModelProperty(notes = "Số PO")
    private String poNumber;
    @ApiModelProperty(notes = "Số hóa đơn")
    private String redInvoiceNo;
    @ApiModelProperty(notes = "Ngày hóa đơn")
    private Date orderDate;
    @ApiModelProperty(notes = "Số nội bộ")
    private String internalNumber;
    private List<ReceiptCreateDetailRequest> lstUpdate;
}
