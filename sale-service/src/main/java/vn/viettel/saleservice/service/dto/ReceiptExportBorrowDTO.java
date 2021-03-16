package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ReceiptExportBorrowDTO extends BaseDTO{
    private String licenseNumber;

    private Timestamp receiptExportBorrowDate;

    private String note;

    private Integer status;
}
