package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreeProductDTO {
    private Long productId;
    private String productName;
    private String productCode;
    private Integer quantity;
    private Integer stockQuantity;
}
