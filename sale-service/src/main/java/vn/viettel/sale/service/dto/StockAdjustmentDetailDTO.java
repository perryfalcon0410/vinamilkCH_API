package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StockAdjustmentDetailDTO extends BaseDTO {
    private Long adjustmentId;
    private Date adjustmentDate;
    private Long shopId;
    private Long productId;
    private String productCode;
    private String productName;
    private String licenseNumber;
    private String unit;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}
