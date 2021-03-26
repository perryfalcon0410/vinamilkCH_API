package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class PromotionDTO {
    private String orderNumber;
    private String customerName;
    private Timestamp createdAt;

}
