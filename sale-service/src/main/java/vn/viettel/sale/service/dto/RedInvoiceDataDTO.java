package vn.viettel.sale.service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedInvoiceDataDTO extends BaseDTO {
    private Long shopId;
    private Long customerId;
    private Long saleOrderId;
    private String customerCode;
    private String customerName;
    private String redInvoiceNumber;
    private Date printDate;
    private String officeWorking;
    private String officeAddress;
    private String taxCode;
    private String createUser;
    private Integer paymentType;
    private Long productId;
    private String productCode;
    private String productName;
    private String uom1;
    private String uom2;
    private Float totalQuantity;
    private Float price;
    private Float priceNotVat;
    private Float amount;
    private Float amountNotVat;
    private Float vat;
    private Float valueAddedTax;
    private String note;
}