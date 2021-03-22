package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SoConfirmDTO extends BaseDTO {
    private Long poConfirmId;
    private Long shopId;
    private String soNo;
    private String productCode;
    private String productName;
    private Float productPrice;
    private Integer quantity;
    private Float priceTotal;
    private Integer isFreeItem;
    private String unit;
}
