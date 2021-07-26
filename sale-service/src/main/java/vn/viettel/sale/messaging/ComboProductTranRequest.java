package vn.viettel.sale.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.core.util.Constants;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;
import vn.viettel.core.validation.annotation.NotEmpty;
import vn.viettel.core.validation.annotation.NotNull;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Request tạo mới xuất nhập combo")
public class ComboProductTranRequest extends BaseRequest {

    @ApiModelProperty(notes = "Ngày giao dịch")
    @NotNull(responseMessage = ResponseMessage.TRANS_DATE_MUST_BE_NOT_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transDate;

    @ApiModelProperty(notes = "Loại giao dịch: 1 nhập combo, 2 xuất combo")
    @NotNull(responseMessage = ResponseMessage.TRANS_TYPE_MUST_BE_NOT_NULL)
    private Integer transType;

    @ApiModelProperty(notes = "Loại kho")
    @NotNull(responseMessage = ResponseMessage.WAREHOUSE_TYPE_ID_MUST_NOT_BE_NUll)
    private Long warehouseTypeId;

    @ApiModelProperty(notes = "Ghi chú")
    @MaxTextLength(length = 3950, responseMessage = ResponseMessage.MAX_LENGTH_STRING)
    private String note;

    @ApiModelProperty(notes = "Danh sách sản phẩm combo")
    @NotNull(responseMessage = ResponseMessage.COMBO_PRODUCT_LIST_BE_NOT_NULL)
    List<@Valid ComboProductTranDetailRequest> details;
}
