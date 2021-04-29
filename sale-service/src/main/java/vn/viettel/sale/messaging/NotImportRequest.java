package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@NoArgsConstructor
public class NotImportRequest extends BaseRequest {
    private Integer reasonDeny;
}