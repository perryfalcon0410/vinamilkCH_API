package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class POConfirmDTO extends BaseDTO{

    private String poNo;
    private String internalNumber;
    private Timestamp poDate;
}
