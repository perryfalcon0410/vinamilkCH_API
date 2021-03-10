package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDTO extends BaseDTO{
    private long saleOrderId;
    private long shopId;
    private String shopCode;
    private long staffId;
    private long customerId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private int orderType;
    private double amount;
    private double discount;
    private double total;
    private int cashierId;
    private String description;
    private String note;
    private double totalWeight;
    private int totalDetail;
    private LocalDateTime timePrint;
    private LocalDateTime stockDate;
    private String createUser;
    private String updateUser;
}
