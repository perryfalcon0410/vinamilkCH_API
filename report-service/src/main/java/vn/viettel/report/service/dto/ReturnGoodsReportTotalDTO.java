package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnGoodsReportTotalDTO {
    private String returnCode;
    private String reciept;
    private String fullName;
    private Integer totalQuantity;
    private Double totalAmount;
    private Double totalRefunds;

    public ReturnGoodsReportTotalDTO(String returnCode, String reciept, String fullName) {
        this.returnCode = returnCode;
        this.reciept = reciept;
        this.fullName = fullName;
    }
}
