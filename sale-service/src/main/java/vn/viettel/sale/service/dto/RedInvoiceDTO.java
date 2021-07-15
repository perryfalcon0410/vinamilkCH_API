package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin hóa đơn đỏ")
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime printDate;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    @ApiModelProperty(notes = "Id khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Loại thanh toán: 0-Tiền mặt, 1-Chuyển khoản")
    private Integer paymentType;
    @ApiModelProperty(notes = "Số đơn hàng(nhiều hơn cách nhau bằng dấu phẩy)")
    private String orderNumbers;
    @ApiModelProperty(notes = "Tổng số lượng")
    private Double totalQuantity;
    @ApiModelProperty(notes = "Tổng thành tiền")
    private Double totalMoney;
    @ApiModelProperty(notes = "Tổng thành tiền không thuế")
    private Double amountNotVat;
    @ApiModelProperty(notes = "Tổng tiền thuế GTGT")
    private Double amountGTGT;

    public Double getTotalQuantity() {
        if(totalQuantity == null) totalQuantity = 0.0;
        return (double)Math.round(totalQuantity);
    }

    public Double getTotalMoney() {
        if(totalMoney == null) totalMoney = 0.0;
        return (double)Math.round(totalMoney);
    }

    public Double getAmountNotVat() {
        if(amountNotVat == null) amountNotVat = 0.0;
        return (double)Math.round(amountNotVat);
    }

    public Double getAmountGTGT() {
        if(amountGTGT == null) amountGTGT = 0.0;
        return (double)Math.round(amountGTGT);
    }
}
