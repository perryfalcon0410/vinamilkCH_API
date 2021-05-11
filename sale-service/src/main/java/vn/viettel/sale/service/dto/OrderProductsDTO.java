package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Cập nhật giá cho các sản phẩm khi đổi loại khách hàng")
public class OrderProductsDTO {

    @ApiModelProperty(notes = "Tổng số lượng sản phẩm trong đơn hàng")
    private int quantity = 0;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private float totalPrice = 0;

    @ApiModelProperty(notes = "Danh sách sản phẩm")
    private List<OrderProductOnlineDTO> products;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void addTotalPrice(float totalPrice) {
        this.totalPrice += totalPrice;
    }
}
