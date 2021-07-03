package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PrintShopImportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;
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
    @ApiModelProperty(notes = "Đơn vị tính thùng")
    @Column(name = "UOM2")
    private String uom2;
    @ApiModelProperty(notes = "Đơn vị tính lẻ")
    @Column(name = "UOM1")
    private String uom1;
    @ApiModelProperty(notes = "Quy đổi")
    @Column(name = "CONVFACT")
    private String convfact;
    @ApiModelProperty(notes = "Mã nhập hàng")
    @Column(name = "TRANS_CODE")
    private String transCode;
    @ApiModelProperty(notes = "Tên cửa hàng")
    @Column(name = "SHOP_NAME")
    private String shopName;
    @ApiModelProperty(notes = "Loại của hàng")
    @Column(name = "TYPE_SHOP")
    private String typeShop;
    @ApiModelProperty(notes = "Nhóm sản phẩm")
    @Column(name = "PRODUCT_GROUP")
    private String productGroup;
    @ApiModelProperty(notes = "id đơn hàng")
    @Column(name = "order_id")
    private Long orderId;
}
