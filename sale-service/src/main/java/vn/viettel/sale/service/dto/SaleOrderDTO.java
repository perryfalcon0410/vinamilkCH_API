package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDTO extends BaseDTO{
    private String orderNumber;
    private String cusNumber;
    private String cusName;
    private double amount;
    private double discount;
    private double accumulation;
    private double total;
    private String note;
    private boolean redReceipt;
    private String comName;
    private String taxCode;
    private String address;
    private String noteRed;
    private Long cusId;
    private Long createdBy;
    private Timestamp saleOrderDate;
}
