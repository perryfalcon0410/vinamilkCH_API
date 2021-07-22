package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Thông tin đơn online")
public class OnlineOrderDTO extends BaseDTO {

    @ApiModelProperty(notes = "Trạng thái của đơn hàng")
    private Integer synStatus;

    @ApiModelProperty(notes = "Số đơn hàng online")
    private String orderNumber;

    @ApiModelProperty(notes = "Thông tin đơn hàng")
    private String orderInfo;

    @ApiModelProperty(notes = "Mã giảm giá")
    private String discountCode;

    @ApiModelProperty(notes = "Giá trị của mã giảm giá")
    private Float discountValue;

    @ApiModelProperty(notes = "Tổng số lượng sản phẩm trong đơn hàng")
    private int quantity = 0;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private float totalPrice = 0;

    @ApiModelProperty(notes = "Ghi chú")
    private String note;

    @ApiModelProperty(notes = "Danh sách sản phẩm")
    private List<OrderProductOnlineDTO> products;

    @ApiModelProperty(notes = "Thông tin khách hàng")
    private List<CustomerDTO> customers;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void addTotalPrice(double totalPrice) {
        this.totalPrice += totalPrice;
    }
}