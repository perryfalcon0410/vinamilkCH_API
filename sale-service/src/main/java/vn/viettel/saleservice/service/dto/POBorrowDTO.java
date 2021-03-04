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

    private String poBorrowNumber;
    private LocalDateTime poDate;
    private String poNote;
}
