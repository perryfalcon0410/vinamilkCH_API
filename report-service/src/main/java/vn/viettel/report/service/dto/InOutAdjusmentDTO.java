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
@AllArgsConstructor
@Entity
public class InOutAdjusmentDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "SHOP_CODE")
    private String shopCode;
    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;
    @Column(name = "ADJUSTMENT_DATE")
    private Date adjustmentDate;
    @Column(name = "TYPESS")
    private String typess;
    @Column(name = "PRODUCT_INFO_NAME")
    private String productInfoName;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "UOM1")
    private String uom1;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "TOTAL")
    private Float total;
    @Column(name = "WAREHOUSE_TYPE_NAME")
    private String warehouseTypeName;
}
