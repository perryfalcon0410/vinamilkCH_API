package vn.viettel.sale.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@NoArgsConstructor
public class NotImportRequest extends BaseRequest {
    @ApiModelProperty("Id lý do không nhập")
    private Integer reasonDeny;
}
