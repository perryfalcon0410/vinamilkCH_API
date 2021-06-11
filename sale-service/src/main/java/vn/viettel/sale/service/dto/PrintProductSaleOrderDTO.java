package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PrintProductSaleOrderDTO {
    private String productName;
    private String productCode;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
    private List<SaleOrderDiscountDTO> discountDTOList;

}
