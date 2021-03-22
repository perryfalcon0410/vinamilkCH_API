package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptExportDetailDTO extends BaseDTO{

    private Long receiptExportId;

    private String productCode;

    private String productName;

    private Float productPrice;

    private String unit;

    private Integer quantity;

    private Float priceTotal;
}
