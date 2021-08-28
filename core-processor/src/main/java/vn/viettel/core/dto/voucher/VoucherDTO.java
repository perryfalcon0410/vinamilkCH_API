package vn.viettel.core.dto.voucher;

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
@ApiModel(description = "Thông tin đơn voucher")
public class VoucherDTO extends BaseDTO {

    @ApiModelProperty(notes = "Mã chương trình")
    private String voucherProgramCode;

    @ApiModelProperty(notes = "Tên chương trình")
    private String voucherProgramName;

    @ApiModelProperty(notes = "Mã voucher")
    private String voucherCode;

    @ApiModelProperty(notes = "Tên voucher")
    private String voucherName;

    @ApiModelProperty(notes = "Số serial")
    private String serial;

    @ApiModelProperty(notes = "Giá")
    private Double price;

    private Long voucherProgramId;

    // FormDate - ToDate of VoucherProgram
    @ApiModelProperty(notes = "Thời gian hiệu lực")
    private String activeTime;;

    private Long shopId;

    private Integer status;

    private Long customerTypeId;

    private String customerTypeCode;

    private Long customerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime changeDate;

    private String changeUser;

    private Boolean activated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime activatedDate;

    private String activatedUser;

    private Boolean isUsed;

    private Long saleOrderId;

    private String orderNumber;

    private Double priceUsed;

    private String orderCustomerCode;

    private Double orderAmount;

    private String orderShopCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

    private Integer paymentStatus;

}
