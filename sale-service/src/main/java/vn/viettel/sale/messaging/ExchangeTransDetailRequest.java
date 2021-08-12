package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;
import vn.viettel.core.validation.annotation.NumberGreaterThanZero;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "")
public class ExchangeTransDetailRequest extends BaseRequest {
    @ApiModelProperty(notes = "")
    private Long id;

    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Đơn vị tính")
    private String unit;

    @ApiModelProperty(notes = "Đơn giá")
    private Double price;

    @ApiModelProperty(notes = "Create = 0 or null | Update = 1 | Delete = 2")
    private Integer type;

    @ApiModelProperty(notes = "Số lượng")
    @NumberGreaterThanZero(responseMessage = ResponseMessage.NUMBER_GREATER_THAN_ZERO)
    @NotNull(responseMessage = ResponseMessage.QUANTITY_CAN_NOT_BE_NULL)
    private Integer quantity;

    @ApiModelProperty(notes = "Tổng tiền")
    private Double totalPrice;
}
