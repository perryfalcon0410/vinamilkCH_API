package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class ReceiptExportAdjustedDetailDTO extends BaseDTO{

    private Long receiptExportAdjustedId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private Float productPrice;
    private String unit;
    private String priceTotal;
}
