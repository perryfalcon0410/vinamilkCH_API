package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class NewOrderReturnDTO {
    private String orderNumber;
    private Date orderDate;
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
    private String radInvoiceAddress;
    private String redInvoiceRemark;
    private Long reasonId;
    private String reasonDesc;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
