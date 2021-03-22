package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

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
