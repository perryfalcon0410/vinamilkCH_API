package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class POPromotionalRequest {
    private ReceiptCreateRequest reccr;
    private PoPromotionalDTO ppd;
    private List<PoPromotionalDetailDTO> ppdds;
}
