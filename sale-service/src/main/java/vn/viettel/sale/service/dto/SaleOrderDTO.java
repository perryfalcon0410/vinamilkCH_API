package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDTO extends BaseDTO {
    private String orderNumber;
    private String customerNumber;
    private String customerName;
    private double amount;
    private double totalPromotion;
    private double customerPurchase;
    private double total;
    private String note;
    private boolean usedRedInvoice;
    private String comName;
    private String taxCode;
    private String address;
    private String redInvoiceRemark;
    private Long customerId;
    private Long createdBy;
    private Date orderDate;
    private String salesManName;
}
