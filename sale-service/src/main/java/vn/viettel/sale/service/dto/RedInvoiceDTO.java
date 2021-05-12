package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RedInvoiceDTO extends BaseDTO {
    @ApiModelProperty(notes = "Số hóa đơn đỏ")
    private String invoiceNumber;
    @ApiModelProperty(notes = "Id Cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Tên đơn vị(VAT)")
    private String officeWorking;
    @ApiModelProperty(notes = "Địa chỉ đơn vị(VAT)")
    private String officeAddress;
    @ApiModelProperty(notes = "Mã số thuế")
    private String taxCode;
    @ApiModelProperty(notes = "Ngày in hóa đơn")
    private Date printDate;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Loại thanh toán: 0-Tiền mặt, 1-Chuyển khoản")
    private Integer paymentType;
    @ApiModelProperty(notes = "Số đơn hàng(nhiều hơn cách nhau bằng dấu phẩy)")
    private String orderNumbers;
    @ApiModelProperty(notes = "Tổng số lượng")
    private Float totalQuantity;
    @ApiModelProperty(notes = "Tổng thành tiền")
    private Float totalMoney;
    @ApiModelProperty(notes = "Tổng thành tiền không thuế")
    private Float amountNotVat;
    @ApiModelProperty(notes = "Tổng tiền thuế GTGT")
    private Float amountGTGT;



}
