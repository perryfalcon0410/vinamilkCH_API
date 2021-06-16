package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Danh sách dữ liệu báo cáo đổi hàng hỏng")
public class ExchangeTransReportDTO<T> {
    @ApiModelProperty(notes = "Định mức")
    T exchangeRate;

    @ApiModelProperty(notes = "Tổng tiền, tổng số lượng")
    Object[] totals;

    @ApiModelProperty(notes = "Danh sách hàng hỏng")
    T response;

    public ExchangeTransReportDTO(Object[] totals) {
        this.totals = totals;
    }
}
