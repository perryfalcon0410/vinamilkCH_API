package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Sản phẩm combo xuất nhập")
public class ComboProductTranDetailRequest extends BaseRequest {
    @ApiModelProperty(notes = "Id sản phẩm combo")
    private Long comboProductId;

    @ApiModelProperty(notes = "Số lượng xuất nhập")
    private Integer quantity;

    @ApiModelProperty(notes = "Giá trên từng sản phẩm")
    private Float price;

}
