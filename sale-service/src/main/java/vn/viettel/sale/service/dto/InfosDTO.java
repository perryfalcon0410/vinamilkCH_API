package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class InfosDTO {
    private String currency;
    private float total;
    private float totalPaid;
    private float balance;
    private String saleMan;
    private String customerName;
    private String orderNumber;
    private Date orderDate;
}
