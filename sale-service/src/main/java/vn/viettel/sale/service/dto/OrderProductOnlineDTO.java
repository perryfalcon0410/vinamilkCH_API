package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin sản phẩm mua")
public class OrderProductOnlineDTO {

    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Giá sản phẩm")
    private Double price;

    @ApiModelProperty(notes = "Số lượng tồn kho hiện tại")
    private Integer stockTotal;

    @ApiModelProperty(notes = "Trạng thái sản phẩm")
    private Integer status;

    @ApiModelProperty(notes = "Đơn vị tính")
    private String uom1;

    @ApiModelProperty(notes = "Sản phẩm combo")
    private Boolean isCombo;

    @ApiModelProperty(notes = "Id sản phẩm combo tương ứng")
    private Long comboProductId;

    @ApiModelProperty(notes = "Số lượng mua")
    private int quantity = 0;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private double totalPrice = 0;

    public void setPrice(Double price) {
        this.price = price;
        this.totalPrice = this.price*this.quantity;
    }

}
