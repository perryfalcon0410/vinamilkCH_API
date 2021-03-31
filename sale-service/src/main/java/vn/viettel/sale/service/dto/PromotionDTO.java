package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class PromotionDTO {
    private String productNumber;
    private String productName;
    private int quantity;
    private String promotionProgramName;
}
