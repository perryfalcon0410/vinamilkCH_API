package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String redInvoiceNo;
    @ApiModelProperty(notes = "Số nội bộ")
    private String internalNumber;
    @ApiModelProperty(notes = "Số PO")
    private String poNumber;
    @ApiModelProperty(notes = "Ngày hóa đơn")
    private Date orderDate;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    private List<ReceiptCreateDetailRequest> lst;

}
