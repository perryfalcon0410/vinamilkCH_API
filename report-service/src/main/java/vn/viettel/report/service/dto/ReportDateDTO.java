package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDateDTO {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date fromDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date toDate;
    private String dateOfPrinting;
    private String shopName;
    private String address;
    private Float totalAmount;
}
