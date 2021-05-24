package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.lang.reflect.Type;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptExportUpdateRequest extends BaseRequest {
    @ApiModelProperty("Loại xuất hàng")
    private Integer type;
    @ApiModelProperty("Ghi chú")
    private String note;
    private List<ReceiptCreateDetailRequest> listProductRemain;
}
