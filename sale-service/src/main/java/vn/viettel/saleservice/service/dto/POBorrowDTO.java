package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class POBorrowDTO extends BaseDTO {

    private String po_borrow_number;
    private LocalDateTime po_date;
    private String po_note;
}
