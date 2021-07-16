package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import vn.viettel.core.util.Constants;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ApiModel(description = "Chi tiết SP nhập hàng")
public class ShopExportDTO {
    @Id
    @Column(name = "ID")
    private Long id;
    @ApiModelProperty(notes = "Phân loại đơn")
    @Column(name = "TYPESS")
    private Integer typess;
    @ApiModelProperty(notes = "ID đơn hàng")
    @Column(name = "ORDER_ID")
    private Long orderId;
    @ApiModelProperty(notes = "Ngày nhập hàng")
    @Column(name = "TRANS_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;
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
    @ApiModelProperty(notes = "ID nghành hàng cha")
    @Column(name = "CAT_ID")
    private Long catId;
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
    @ApiModelProperty(notes = "Giá chưa thuế - Trong in Po lấy giá này")
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @ApiModelProperty(notes = "Tổng thành tiền giá trước thuế - Trong in Po lấy giá này ")
    @Column(name = "TOTAL_PRICE_NOT_VAT")
    private Float totalPriceNotVat;
    @ApiModelProperty(notes = "Giá có thuế - Trong in nhập điều chỉnh/xuất điều chỉnh lấy giá này")
    @Column(name = "PRICE")
    private Float price;
    @ApiModelProperty(notes = "Tổng thành tiền giá sau thuế - Trong in nhập điều chỉnh/xuất điều chỉnh lấy giá này")
    @Column(name = "TOTAL_PRICE_VAT")
    private Float totalPriceVat;
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
    @ApiModelProperty(notes = "Ghi chú")
    @Column(name = "NOTE")
    private String note;
    @ApiModelProperty(notes = "Mã đơn trả PO")
    @Column(name = "RETURN_CODE")
    private String returnCode;
}
