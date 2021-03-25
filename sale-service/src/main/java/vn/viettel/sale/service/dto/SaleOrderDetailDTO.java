package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDetailDTO extends BaseDTO{
    private List<OrderDetailDTO> orderDetail;
    private PaymentDTO payment;
    private List<PromotionDTO> promotion;
    private List<DiscountDTO> discount;
}
