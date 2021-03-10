package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDetailDTO extends BaseDTO{
    private long saleOrderDetailId;
    private long saleOrderId;
    private LocalDateTime orderDate;
    private long shopId;
    private long staffId;
    private long productId;
    private int convfact;
    private int catId;
    private int quantity;
    private int quantityRetail;
    private int quantityPackage;
    private int isFreeItem;
    private double discountPercent;
    private double discountAmount;
    private double amount;
    private int priceId;
    private double price;
    private double priceNotVat;
    private double packagePrice;
    private double packagePriceNotVat;
    private int vat;
    private double totalWeight;
    private String programeTypeCode;
    private String createUser;
    private String updateUser;
}
