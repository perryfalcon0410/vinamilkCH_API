package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrintProductSaleOrderDTO {
    private String productName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}
