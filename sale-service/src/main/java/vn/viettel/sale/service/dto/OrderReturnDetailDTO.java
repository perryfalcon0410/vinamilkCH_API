package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.sale.controller.PromotionReturnDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDetailDTO {
    private Date orderDate;
    private String CustomerName;
    private String reason;
    private Date returnDate;
    private String userName;
    private List<ProductReturnDTO> productReturn;
    private List<PromotionReturnDTO> promotionReturn;
}
