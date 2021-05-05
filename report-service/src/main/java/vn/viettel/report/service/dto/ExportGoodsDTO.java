package vn.viettel.report.service.dto;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Data
@ToString
@NamedStoredProcedureQuery(name = "P_EXPORT_GOODS", procedureName = "P_EXPORT_GOODS", resultClasses = {
        ExportGoodsDTO.class })
public class ExportGoodsDTO{
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "EXPORT_DATE")
    private Timestamp exportDate;
    @Column(name = "TRAN_CODE")
    private String tranCode;
    @Column(name = "EXPORT_TYPE")
    private String exportType;
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @Column(name = "ORDER_DATE")
    private Timestamp orderDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @Column(name = "NOTED")
    private String noted;
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "PACKAGE_QUANTITY")
    private Integer packetQuantity;
    @Column(name = "UNIT_QUANTITY")
    private Integer unitQuantity;
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;
    @Column(name = "AMOUNT_NOT_VAT")
    private Float amountNotVat;
    @Column(name = "CONVFACT")
    private Integer convfact;
    @Column(name = "PACKAGE_UNIT")
    private String packetUnit;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "SHOP_NAME")
    private String shopName;
    @Column(name = "SHOP_TYPE")
    private String shopType;
}
