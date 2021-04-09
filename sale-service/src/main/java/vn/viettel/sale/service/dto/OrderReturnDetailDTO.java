package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDetailDTO {
    private Date orderDate;
    private String CustomerName;
    private long reasonId;
    private String reasonDesc;
    private Date returnDate;
    private String userName;
    private String note;
    private List<ProductReturnDTO> productReturn;
    private List<PromotionReturnDTO> promotionReturn;
}
