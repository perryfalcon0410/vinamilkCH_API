package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class HddtExcel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "BUYER_NAME")
    private String buyerName;
    @Column(name = "OFFICE_WORKING")
    private String officeWorking;
    @Column(name = "OFFICE_ADDRESS")
    private String officeAddress;
    @Column(name = "TAX_CODE")
    private String taxCode;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    @Column(name = "PAYMENT_TYPE")
    private Integer paymentType;
    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;
    @Column(name = "ORDER_NUMBER")
    private String orderNumbers;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "UOM1")
    private String uom1;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE_NOT_VAT")
    private Double priceNotVat;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "NOTE")
    private String note;

    public HddtExcel(Long id,
                     String buyerName,
                     String officeWorking,
                     String officeAddress,
                     String taxCode, Long shopId, Long customerId, Integer paymentType, String orderNumbers, String invoiceNumber,
                     String productName, String productCode, String uom1, Integer quantity, Float priceNotVat,
                     Float price, String note) {
        this.id = id;
        this.buyerName = buyerName;
        this.officeWorking = officeWorking;
        this.officeAddress = officeAddress;
        this.taxCode = taxCode;
        this.shopId = shopId;
        this.customerId = customerId;
        this.paymentType = paymentType;
        this.orderNumbers = orderNumbers;
        this.invoiceNumber = invoiceNumber;
        this.productName = productName;
        this.productCode = productCode;
        this.uom1 = uom1;
        this.quantity = quantity;
        this.priceNotVat = priceNotVat == null ? 0:priceNotVat.doubleValue();
        this.price = price== null ? 0:price.doubleValue();
        this.note = note;
    }
}
