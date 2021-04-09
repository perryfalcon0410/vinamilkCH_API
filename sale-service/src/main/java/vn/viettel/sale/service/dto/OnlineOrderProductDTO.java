package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnlineOrderProductDTO {

    private Long id;

    private String productName;

    private String productCode;

    private String uom1;

    private Integer safetyStock;

    private Integer quantity;



}
