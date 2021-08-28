
package vn.viettel.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDTO extends BaseDTO {
    private String orderNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
    private Long shopId;
    private Long salemanId;
    private Long customerId;
    private Long wareHouseTypeId;
    private Float amount;
    private Float totalPromotion;
    private Float total;
    private Float totalPaid;
    private Float balance;
    private String note;
    private Integer type;
    private Long fromSaleOrderId;
    private Float memberCardAmount;
    private Float totalVoucher;
    private Integer paymentType;
    private Integer deliveryType;
    private Float totalCustomerPurchase;
    private Integer orderType;
    private String onlineNumber;
    private Float autoPromotionNotVat;
    private Float autoPromotionVat;
    private Float autoPromotion;
    private Float zmPromotion;
    private Float totalPromotionNotVat;
    private Float customerPurchase;
    private String f1Number;
    private Float discountCodeAmount;
    private Integer onlineSubType;
    private Boolean usedRedInvoice;
    private String redInvoiceCompanyName;
    private String redInvoiceTaxCode;
    private String redInvoiceAddress;
    private String redInvoiceRemark;
    private Long reasonId;
    private String reasonDesc;

}
