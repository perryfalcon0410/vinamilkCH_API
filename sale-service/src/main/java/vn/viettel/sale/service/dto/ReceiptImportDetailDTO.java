package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptImportDetailDTO extends BaseDTO{

    private Long receiptImportId;
    private String productCode;
    private String productName;
    private Float productPrice;
    private String unit;
    private Float priceTotal;
    private Integer quantity;
    private Integer quantityExport;
    private String exportOfAll;
    private String remainOfAll;
}
