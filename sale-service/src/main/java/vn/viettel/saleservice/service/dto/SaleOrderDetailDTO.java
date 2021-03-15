package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDetailDTO extends BaseDTO{
    // sale order detail
    private long productId;
    private String productCode;
    private String productName;
    private String unit;
    private int quantity;
    private float price;
    private float totalPrice;
    private float discount;
    private float payment;
    private String note;

    //info payment
    private int totalAmount;
    private float totalPriceAmount;
    private float priceDiscount;
    private String paymentMethod;
    private float paid;
    private float change;

    //info discount;
    private float cumulativePoint;
    private float exchangePoint;
    private String voucherCode;
    private float voucherPrice;
    private String discountCode;
    private float discountPrice;
    private float totalDiscount;

    //info promotion
    private long promotionId;
    private String promoProductCode;
    private String promoProductName;
    private int promoQuantity;
    private int maxPromoQuantity;

}
