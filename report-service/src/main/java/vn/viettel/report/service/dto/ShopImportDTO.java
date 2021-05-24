package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "Ngày nhập hàng")
    @Column(name = "TRANS_DATE")
    private Date transDate;
    @ApiModelProperty(notes = "Loại nhập hàng")
    @Column(name = "IMPORT_TYPE")
    private String importType;
    @ApiModelProperty(notes = "Số hóa đơn")
    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;
    @ApiModelProperty(notes = "Số PO")
    @Column(name = "PO_NUMBER")
    private String poNumber;
    @ApiModelProperty(notes = "Số nội bộ")
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;
    @ApiModelProperty(notes = "Ngày hóa đơn")
    @Column(name = "ORDER_DATE")
    private Date orderDate;
    @ApiModelProperty(notes = "Ngành hàng")
    @Column(name = "PRODUCT_INFO_NAME")
    private String productInfoName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @ApiModelProperty(notes = "Số lượng")
    @Column(name = "QUANTITY")
    private Integer quantity;
    @ApiModelProperty(notes = "Số lượng sỉ")
    @Column(name = "WHOLESALE")
    private Integer wholesale;
    @ApiModelProperty(notes = "Số lượng lẻ")
    @Column(name = "RETAIL")
    private Integer retail;
    @ApiModelProperty(notes = "Giá chưa thuế")
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @ApiModelProperty(notes = "Thành tiền")
    @Column(name = "AMOUNT")
    private Float amount;
    @ApiModelProperty(notes = "Giá")
    @Column(name = "PRICE")
    private Float price;
    @ApiModelProperty(notes = "Tổng cộng")
    @Column(name = "TOTAL")
    private Float total;
    @Column(name = "UOM2")
    private String uom2;
    @Column(name = "UOM1")
    private String uom1;
    @Column(name = "CONVFACT")
    private String convfact;
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
