package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.WareHouse;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class ReceiptCreateRequest extends BaseDTO {
    private String receipt_code;
    private String receipt_type;
    private String invoice_number;
    private String internal_number;
    private LocalDateTime invoice_date;
    private String note;
    private WareHouseDTO warehouseDTO;
    private POAdjustedDTO poAdjustedDTO;
    private POConfirmDTO poConfirmDTO;
    private POBorrowDTO poBorrowDTO;
}
