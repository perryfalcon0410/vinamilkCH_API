package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.sale.entities.SaleOrderComboDetail;
import vn.viettel.sale.entities.SaleOrderDetail;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverOrderDetailDTO {
    private List<SaleOrderDetail> listOrderDetail;
    private List<SaleOrderComboDetail> listOrderComboDetail;
    private List<OrderDetailShopMapDTO> listPromotions;
    private List<Long> productIds;
    private Integer totalQuantity;
    private Double totalAmount;
}
