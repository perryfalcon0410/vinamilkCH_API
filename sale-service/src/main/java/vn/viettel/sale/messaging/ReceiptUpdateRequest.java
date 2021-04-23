package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptUpdateRequest extends BaseRequest {
    private Integer type;
    private String note;
    private List<Long> idRemove;
    private List<ReceiptCreateDetailRequest> lstUpdate;
}
