package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDetailDTO extends BaseDTO {
    private List<OrderDetailDTO> orderDetail;
    private List<PromotionDTO> promotion;
    private List<DiscountDTO> discount;
    private String currency;
    private float amount;
    private float total;
    private float totalPaid;
    private float balance;
    private String saleMan;
}
