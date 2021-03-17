package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDTO extends BaseDTO{
    private String orderNumber;
    private String cusNumber;
    private String cusName;
    private double total;
    private double discount;
    private double accumulation;
    private double paid;
    private String note;
    private boolean redReceipt;
    private String comName;
    private String taxCode;
    private String address;
    private String noteRed;
    private double cusId;


    public SaleOrderDTO(String orderNumber, String cusNumber, String cusName, double total, double discount, double accumulation, double paid, String note, boolean redReceipt, String comName, String taxCode, String address, String noteRed) {
        this.orderNumber = orderNumber;
        this.cusNumber = cusNumber;
        this.cusName = cusName;
        this.total = total;
        this.discount = discount;
        this.accumulation = accumulation;
        this.paid = paid;
        this.note = note;
        this.redReceipt = redReceipt;
        this.comName = comName;
        this.taxCode = taxCode;
        this.address = address;
        this.noteRed = noteRed;
    }
}
