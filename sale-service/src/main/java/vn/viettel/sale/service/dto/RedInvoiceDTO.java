package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RedInvoiceDTO extends BaseDTO {
    private String invoiceNumber;
    private Long shopId;
    private String officeWorking;
    private String officeAddress;
    private String taxCode;
    private Float totalQuantity;
    private Float totalMoney;
    private Date printDate;
    private String note;
    private Long customerId;
    private Integer paymentType;
    private String orderNumbers;

    private Float amountNotVat;
    private Float amountGTGT;



}
