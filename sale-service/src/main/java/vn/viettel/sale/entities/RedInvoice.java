package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
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
    private Date printDate;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "PAYMENT_TYPE")
    private Integer paymentType;
    @Column(name = "ORDER_NUMBER")
    private String orderNumbers;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
