package vn.viettel.sale.entities;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RED_INVOICES")
public class RedInvoice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
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
    private LocalDateTime printDate;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "PAYMENT_TYPE")
    private Integer paymentType;
    @Column(name = "ORDER_NUMBER")
    private String orderNumbers;
    @Column(name = "BUYER_NAME")
    private String buyerName;

    @Formula("(SELECT sum(ex.AMOUNT) FROM RED_INVOICE_DETAILS ex WHERE ex.RED_INVOICE_ID = ID )")
    private Double amountNotVat;

    @Formula(value = "(SELECT sum(ex.AMOUNT - ex.AMOUNT_NOT_VAT) FROM RED_INVOICE_DETAILS ex WHERE ex.RED_INVOICE_ID = ID )")
    private Double amountGTGT;
}
