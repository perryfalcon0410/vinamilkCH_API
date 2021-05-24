package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateDetailRequest extends BaseRequest {
    @ApiModelProperty(notes = "Id chi tiết đơn nhập hàng")
    private Long id;
    @ApiModelProperty(notes = "Id đơn nhập hàng")
    private Long transId;
    @ApiModelProperty(notes = "Ngày nhập")
    private Date transDate;
    @ApiModelProperty(notes = "Id cửa hàng")
    private Long shopId;
    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Giá")
    private Float price;
    @ApiModelProperty(notes = "Giá trước thuế")
    private Float priceNotVat;
    @ApiModelProperty(notes = "Thành tiền")
    private Float amount;
    @ApiModelProperty(notes = "Thành tiền trước thuế")
    private Float amountNotVat;


}
