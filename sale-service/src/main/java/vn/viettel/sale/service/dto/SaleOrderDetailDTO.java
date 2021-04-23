package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleOrderDetailDTO {
    private List<OrderDetailDTO> orderDetail;
    private List<DiscountDTO> discount;
    private List<PromotionDTO> promotion;
    private InfosDTO infos;
}
