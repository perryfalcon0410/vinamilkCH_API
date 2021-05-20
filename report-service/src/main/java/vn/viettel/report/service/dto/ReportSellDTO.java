package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportSellDTO {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date fromDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date toDate;
    private String dateOfPrinting;
    private String shopName;
    private String address;
    private String tel;
    private Integer someBills;
    private Integer totalQuantity;
    private Float totalTotal;
    private Float totalPromotion;
    private Float totalPay;
}
