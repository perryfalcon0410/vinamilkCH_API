package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RED_INVOICE")
public class RedInvoice extends BaseEntity{
    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "OFFICE_WORKING")
    private String officeWorking;
    @Column(name = "OFFICE_ADDRESS")
    private String officeAddress;
    @Column(name = "TAX_CODE")
    private String taxCode;
    @Column(name = "TOTAL_QUANTITY")
    private Float totalQuantity;
    @Column(name = "TOTAL_MONEY")
    private Float totalMoney;
    @Column(name = "PRINT_DATE")
    private Date printDate;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "PAYMENT_TYPE")
    private Integer paymentType;
    @Column(name = "ORDER_NUMBERS")
    private Integer orderNumbers;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
