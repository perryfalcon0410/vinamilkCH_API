package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDetailDTO {
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Số lượng mua")
    private Integer quantity;
    @ApiModelProperty(notes = "Giá sản phẩm")
    private Double PricePerUnit;
    @ApiModelProperty(notes = "Tổng tiền")
    private Double amount;
    @ApiModelProperty(notes = "Giảm giá")
    private Double discount;
    @ApiModelProperty(notes = "Tiền khách đưa")
    private Double payment;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Đơn vị")
    private String unit;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;
    @ApiModelProperty(notes = "Trường hàng khuyến mãi")
    private Boolean isFreeItem;
    @ApiModelProperty(notes = "Khuyến mãi tay giảm tền")
    private Double zmPromotion;
    @ApiModelProperty(notes = "Mã chương trình khuyến mãi")
    private String promotionCode;
    private Integer levelNumber;

}
