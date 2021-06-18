package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin đơn hàng")
public class ReportProductTransDetailDTO extends BaseDTO {

    @ApiModelProperty(notes = "Loại đơn hàng 0,1,2")
    private Integer transType;

    @ApiModelProperty(notes = "Mã giao dịch")
    private String transCode;

    @ApiModelProperty(notes = "Số PO")
    private String poNumber;

    @ApiModelProperty(notes = "Số hóa đơn")
    private String invoiceNumber;

    @ApiModelProperty(notes = "Ngày nhập")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;

    @ApiModelProperty(notes = "Số nội bộ")
    private String internalNumber;

    @ApiModelProperty(notes = "Ngày hóa đơn")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity = 0;

    @ApiModelProperty(notes = "Tổng tiền trước thuế")
    private Double totalPriceNotVat = 0D;

    @ApiModelProperty(notes = "Tổng tiền thuế = Tổng tiền sau thuế - tổng tiền trước thuế")
    private Double totalPriceVat = 0D;

    @ApiModelProperty(notes = "Tổng tiền sau thuế")
    private Double totalPrice = 0D;

    private String note;

    public Double addTotalPriceNotVat(Double totalPriceNotVat) {
        this.totalPriceNotVat += totalPriceNotVat;
        return this.totalPriceNotVat;
    }

    public Double addTotalPrice(Double totalPrice) {
        this.totalPrice += totalPrice;
        return this.totalPrice;
    }

    public Integer addTotalQuantity(Integer quantity) {
        this.totalQuantity += quantity;
        return this.totalQuantity;
    }


}
