package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalRedInvoiceResponse {
    @ApiModelProperty(notes = "Tổng số lượng ")
    private Float totalQuantity = 0F;
    @ApiModelProperty(notes = "Tổng thành tiền ")
    private Double totalAmount = 0D;
    @ApiModelProperty(notes = "Tổng tiền thuế giá trị gia tăng ")
    private Double totalValueAddedTax = 0D;
    @ApiModelProperty(notes = "ID shop ")
    private Long shopId;
    @ApiModelProperty(notes = "ID khách hàng")
    private Long customerId;
    @ApiModelProperty(notes = "Mã khách hàng")
    private String customerCode;
    @ApiModelProperty(notes = "Tên khách hàng")
    private String customerName;
    @ApiModelProperty(notes = "Số hóa đơn đỏ")
    private String redInvoiceNumber;
    @ApiModelProperty(notes = "Ngày tạo hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime printDate;
    @ApiModelProperty(notes = "Tên đơn vị(VAT)")
    private String officeWorking;
    @ApiModelProperty(notes = "Địa chỉ đơn vị(VAT)")
    private String officeAddress;
    @ApiModelProperty(notes = "Mã số thuế")
    private String taxCode;
    @ApiModelProperty(notes = "Loại thanh toán")
    private Integer paymentType;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
}
