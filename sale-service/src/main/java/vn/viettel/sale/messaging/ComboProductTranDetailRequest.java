package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Sản phẩm combo xuất nhập")
public class ComboProductTranDetailRequest extends BaseRequest {
    @ApiModelProperty(notes = "Id sản phẩm combo")
    @NotNull(responseMessage = ResponseMessage.COMBO_PRODUCT_ID_MUST_BE_NOT_NULL)
    private Long comboProductId;

    @ApiModelProperty(notes = "Số lượng xuất nhập")
    @NotNull(responseMessage = ResponseMessage.QUANTITY_MUST_BE_NOT_NULL)
    private Integer quantity;

    @ApiModelProperty(notes = "Giá trên từng sản phẩm")
    private Float price;

    private Float priceNotVAT;

    private Long refProductId;

}
