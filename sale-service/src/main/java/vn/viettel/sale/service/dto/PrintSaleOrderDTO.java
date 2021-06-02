package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PrintSaleOrderDTO {
    private String orderNumber;
    private String customerName;
    private String customerPhone;
    private String address;
    private Integer deliveryType;
    private Float customerPurchase;
    private LocalDateTime orderDate;
    private String userName;
    private List<PrintProductSaleOrderDTO> products;
    private Double amount;
    private Double amountNotVAT;
    private Double totalPromotionNotVat;
    private Double discountCodeAmount;
    private Double total;
    private Double totalPaid;
    private String nameShop;
    private String shopAddress;
    private String phone;
}
