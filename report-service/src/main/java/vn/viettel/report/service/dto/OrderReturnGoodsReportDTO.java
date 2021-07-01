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
public class OrderReturnGoodsReportDTO {
    private String returnNumber;
    private String orderNumber;
    private String customerName;
    private List<ReturnGoodsDTO> reportPrintProductDTOS;
}
