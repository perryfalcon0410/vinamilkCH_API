package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.sql.Timestamp;

@Getter
@Setter
public class ReceiptExportAdjustedDTO extends BaseDTO{

    private String licenseNumber;

    private Timestamp receiptExportAdjustedDate;

    private String note;

    private Integer status;
}
