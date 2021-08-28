package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class StockBorrowingDetailDTO extends BaseDTO {
    private Long borrowingId;
    private Long shopId;
    private Long productId;
    private String productCode;
    private String productName;
    private String licenseNumber;
    private String unit;
    private Integer quantity;
    private Float price;
    private Float totalPrice;
}
