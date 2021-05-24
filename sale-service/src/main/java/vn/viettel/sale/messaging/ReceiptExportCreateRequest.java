package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.Date;
import java.util.List;

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
    private Date transDate;
    @ApiModelProperty("Loại xuất")
    private Integer type;
    @ApiModelProperty("Số hóa đơn")
    private String redInvoiceNo;
    @ApiModelProperty("Số nội bộ")
    private String internalNumber;
    @ApiModelProperty("Số PO")
    private String poNumber;
    @ApiModelProperty("Ngày hóa đơn")
    private Date orderDate;
    @ApiModelProperty("Ghi chú")
    private String note;
}
