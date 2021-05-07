package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedInvoicePrint {

    //red invoice
    private String buyerName;
    private String officeWorking;
    private String officeAddress;
    private String taxCode;
    private Integer paymentType;
    private String orderNumbers;
    private Float totalMoney;
    private String invoiceNumber;
    private String note;
    private Date printDate;

    private String shopCode;

    //customer
    private String customerCode;
    private String mobiPhone;
    private String firstName;
    private String lastName;

    //product
    private String productCode;
    private String productName;
    private String uom1;

    //red invoice detail
    private Integer quantity;
    private Float priceNotVat;
    private Float totalAmount;//quantity*priceNotVat
    private Float GTGT;
    private String wareHouse;//default null

}
