package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportSellDTO {

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fromDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate toDate;
    private String dateOfPrinting;
    private String shopName;
    private String address;
    private String tel;
    private Integer someBills;
    private Long totalQuantity;
    private Double totalTotal;
    private Double totalPromotionNotVat;
    private Double totalPromotion;
    private Double totalPay;
}
