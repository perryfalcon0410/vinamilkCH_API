package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptExportBorrowDetailDTO extends BaseDTO{
    private Long receiptExportBorrowId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private Float productPrice;
    private String unit;
    private Float priceTotal;
}
