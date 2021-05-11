package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PrintGoodDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "EXPORT_TYPE")
    private String exportType;
    @Column(name = "EXPORT_DATE")
    private Timestamp exportDate;
    @Column(name = "TRAN_CODE")
    private String tranCode;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "ORDER_DATE")
    private Timestamp orderDate;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "PRODUCT_CATEGORY")
    private String productCategory;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "UNIT")
    private String unit;
}
