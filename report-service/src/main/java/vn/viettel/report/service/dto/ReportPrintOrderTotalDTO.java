package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportPrintOrderTotalDTO {
    private String returnCode;
    private String orderCode;
    private String customerName;
    private Integer totalQuantity;
    private Float totalAmount;
    private Float totalRefunds;
    private List<ReportPrintProductDTO> reportPrintProductDTOS;

}
