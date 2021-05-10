package vn.viettel.core.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDTO extends BaseDTO {
    private String voucherProgramCode;

    private String voucherProgramName;

    private String voucherCode;

    private String voucherName;

    private String serial;

    private Float price;

    private Long voucherProgramId;

    // FormDate - ToDate of VoucherProgram
    private String activeTime;

    private Long shopId;

    private Integer status;

    private Long customerTypeId;

    private String customerTypeCode;

    private Long customerId;

    private Date changeDate;

    private String changeUser;

    private Boolean activated;

    private Date activatedDate;

    private String activatedUser;

    private Boolean isUsed;

    private Long saleOrderId;

    private String orderNumber;

    private Float priceUsed;

    private String orderCustomerCode;

    private Float orderAmount;

    private String orderShopCode;

    private Date orderDate;

    private Integer paymentStatus;

    private String updateUser;
}
