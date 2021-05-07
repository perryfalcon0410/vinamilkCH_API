package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Date orderDate;
    private String userName;
    private List<PrintProductSaleOrderDTO> products;
    private Float amount;
    private Float amountNotVAT;
    private Float totalPromotionNotVat;
    private Float discountCodeAmount;
    private Float total;
    private Float totalPaid;
}
