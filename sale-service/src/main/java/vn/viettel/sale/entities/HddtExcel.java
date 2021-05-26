package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

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
    private Float priceNotVat;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "NOTE")
    private String note;

}
