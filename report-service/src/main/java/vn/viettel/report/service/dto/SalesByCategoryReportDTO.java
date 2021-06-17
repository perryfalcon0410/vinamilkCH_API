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
@ApiModel(description = "Danh sách báo cáo doanh số theo ngành hàng")
public class SalesByCategoryReportDTO<T> {
    @ApiModelProperty(notes = "Tổng tiền, tổng số lượng")
    Object[] totals;

    @ApiModelProperty(notes = "Danh sách doanh số theo ngành hàng")
    T response;

    public SalesByCategoryReportDTO(Object[] totals) {
        this.totals = totals;
    }
}
