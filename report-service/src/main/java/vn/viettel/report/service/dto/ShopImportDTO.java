package vn.viettel.report.service.dto;

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
public class ShopImportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TRANS_DATE")
    private Date transDate;
    @Column(name = "IMPORT_TYPE")
    private String importType;
    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @Column(name = "PRODUCT_INFO_NAME")
    private String productInfoName;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "WHOLESALE")
    private Integer wholesale;
    @Column(name = "RETAIL")
    private Integer retail;
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @Column(name = "AMOUNT")
    private Float AMOUNT;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "TOTAL")
    private Float total;
    @Column(name = "UOM2")
    private String uom2;
    @Column(name = "UOM1")
    private String uom1;
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "SHOP_NAME")
    private String shopName;
    @Column(name = "TYPE_SHOP")
    private String typeShop;
    @Column(name = "PRODUCT_GROUP")
    private String productGroup;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "RETURN_CODE")
    private String returnCode;
}
