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
    private String category;
    private Integer totalQuantity;
    private Float totalAmount;
    private Float totalRefunds;
    private List<OrderReturnGoodsReportDTO> orderReturnGoods;
}
