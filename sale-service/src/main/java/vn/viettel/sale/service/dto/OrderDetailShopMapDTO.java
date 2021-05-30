package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.sale.entities.SaleOrderDetail;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailShopMapDTO {
    private Boolean isAuto;
    private Long promotionProgramId;
    private String promotionProgramCode;
    private String promotionProgramName;
    private Integer relation;
    private Float discount;
    private SaleOrderDetail saleOrderDetail;
}
