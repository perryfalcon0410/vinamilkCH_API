package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private LocalDateTime printDate;

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
    @ApiModelProperty(notes = "Tổng thành tiền = Số lượng * Tiền không thuế")
    private Float totalAmount;
    @ApiModelProperty(notes = "Phần trăm thuế GTGT")
    private Float GTGT;
    @ApiModelProperty(notes = "Kho")
    private String wareHouse;

}
