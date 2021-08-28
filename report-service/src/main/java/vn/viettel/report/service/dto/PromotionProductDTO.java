package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ApiModel(description = "Thông tin hàng khuyến mãi")
public class PromotionProductDTO implements Cloneable{
    @Id
    @Column(name = "ID")
    private Long id;
    @ApiModelProperty(notes = "Ngày bán")
    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;
    @ApiModelProperty(notes = "Id ngành hàng")
    @Column(name = "PRODUCT_CAT_ID")
    private Long productCatId;
    @ApiModelProperty(notes = "Mã ngành hàng")
    @Column(name = "PRODUCT_CAT_NAME")
    private String productCatName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    @Column(name = "PRODUCT_CODE")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @ApiModelProperty(notes = "Mã hóa đơn")
    @Column(name = "ORDER_NUMBER")
    private String orderNumber;
    @ApiModelProperty(notes = "Số lượng")
    @Column(name = "QUANTITY")
    private Integer quantity;
    @ApiModelProperty(notes = "Mã vạch")
    @Column(name = "BAR_CODE")
    private String barCode;
    @ApiModelProperty(notes = "Đơn vị tính")
    @Column(name = "UOM")
    private String uom;
    @ApiModelProperty(notes = "Mã chương trình khuyến mãi")
    @Column(name = "PROMOTION_CODE")
    private String promotionCode;
    @ApiModelProperty(notes = "Số đơn online")
    @Column(name = "ONLINE_NUMBER")
    private String onlineNumber;
    @ApiModelProperty(notes = "Loại - kênh bán")
    @Column(name = "ORDER_TYPE")
    private String orderType;

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
