package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ReportVoucherDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "SHOP_CODE")
    private String shopCode;
    @Column(name = "SHOP_NAME")
    private String shopName;
    @Column(name = "VOUCHER_PROGRAM_NAME")
    private String voucherProgramName;
    @Column(name = "VOUCHER_CODE")
    private String voucherCode;
    @Column(name = "VOUCHER_NAME")
    private String voucherName;
    @Column(name = "SERIAL")
    private String serial;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "CHANGE_USER")
    private String changeUser;
    @Column(name = "CHANGE_DATE")
    private Date changeDate;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "ACTIVATED")
    private String activated;
    @Column(name = "IS_USED")
    private String isUsed;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "ORDER_SHOP_CODE")
    private String orderShopCode;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "ORDER_AMOUNT")
    private Float orderAmount;

}
