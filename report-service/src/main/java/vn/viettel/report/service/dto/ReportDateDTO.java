package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDateDTO {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fromDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate toDate;
    private String dateOfPrinting;
    private String shopName;
    private String address;
    private Float totalAmount;
}
