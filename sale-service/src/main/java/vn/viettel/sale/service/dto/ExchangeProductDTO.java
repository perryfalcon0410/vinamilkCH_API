package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeProductDTO {
    private Long id;
    private String productCode;
    private String productName;
    private Float price;
    private Integer quantity;
    private Float totalAmount;
}
