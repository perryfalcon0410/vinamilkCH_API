package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReturnGoodsReportDTO {

    private String productCode;

    private String productName;

    private String unit;

    private Integer quantity;

    private Float price;

    private Float amount;

    private Float totalAmount;

    private Float refunds;


}
