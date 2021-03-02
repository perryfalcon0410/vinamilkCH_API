package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class POConfirmDTO extends BaseDTO{

    private String po_no;
    private String internal_number;
    private LocalDateTime po_date;
}
