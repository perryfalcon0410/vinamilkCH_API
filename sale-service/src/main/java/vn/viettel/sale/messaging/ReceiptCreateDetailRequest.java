package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReceiptCreateDetailRequest extends BaseRequest {
    private Long transId;
    private Date transDat;
    private Long shopId;
    private Long productId;
    private Integer quantity;
    private Float price;
    private Float priceNotVat;
    private Float amount;
    private Float amountNotVat;


}
