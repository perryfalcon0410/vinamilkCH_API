package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PoPromotionalDetailDTO extends BaseDTO{

    private Long poPromotionalId;

    private String productCode;

    private Integer quantity;

    private Float productPrice;

    private Long productId;

    private String productName;

    private String unit;

    private String soNo;

    private Float priceTotal;
}
