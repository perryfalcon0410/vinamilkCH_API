package vn.viettel.sale.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;
import vn.viettel.sale.service.dto.ComboProductTranDetailRequest;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComboProductTranRequest extends BaseRequest {

//    private String transCode;

    private Date transDate;

    private Integer transType;

    private String note;

    List<ComboProductTranDetailRequest> details;
}
