package vn.viettel.saleservice.service.dto;

import com.amazonaws.services.dynamodbv2.xspec.B;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptImportDTO extends BaseDTO {

    private String receipt_code;
    private Integer receipt_quantity;
    private String invoice_number;
    private String internal_number;
    private LocalDateTime receipt_date;
    private LocalDateTime invoice_date;
    private Float receipt_total;
    private String note;
    private String receipt_type;
    private Integer status;


}
