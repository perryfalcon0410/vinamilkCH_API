package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StockBorrowingTransDetailDTO extends BaseDTO {
    private Long transId;
    private LocalDateTime transDate;
    private Long shopId;
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    private Integer quantity;
    private Float price;
    private Float totalPrice;
    private Float priceNotVat;
}
