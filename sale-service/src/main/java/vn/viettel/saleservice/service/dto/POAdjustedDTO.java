package vn.viettel.saleservice.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class POAdjustedDTO extends BaseDTO{
    private String poLicenseNumber;
    private Timestamp poDate;
    private String poNote;
}
