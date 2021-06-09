package vn.viettel.promotion.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.validation.annotation.NotBlank;

import java.time.LocalDateTime;
import java.util.Date;

public class VoucherUpdateRequest extends BaseRequest {

    @NotBlank(responseMessage = ResponseMessage.VOUCHER_CODE_MUST_BE_NOT_BLANK)
    private String voucherCode;

    @NotBlank(responseMessage = ResponseMessage.VOUCHER_NAME_MUST_BE_NOT_BLANK)
    private String voucherName;

    @NotBlank(responseMessage = ResponseMessage.VOUCHER_SERIAL_MUST_BE_NOT_BLANK)
    private Long voucherProgramId;

    private String serial;

    private Float price;

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

    private Float priceUsed;

    private String orderCustomerCode;

    private Float orderAmount;

    private String orderShopCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

    private Integer paymentStatus;

}
