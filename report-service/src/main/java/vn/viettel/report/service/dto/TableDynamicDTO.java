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
@ApiModel(description = "Danh sách dữ liệu báo cáo doanh số hóa đơn, số lượng đơn theo khách hàng")
public class TableDynamicDTO<T> {
    @ApiModelProperty(notes = "Ngày hóa đơn")
    List<String> dates;

    @ApiModelProperty(notes = "Tổng tiền, tổng số lượng")
    Object[] totals;

    @ApiModelProperty(notes = "Danh sách khách hàng theo số hóa đơn, số lượng đơn")
    T response;

    public TableDynamicDTO(List<String> dates, Object[] totals) {
        this.dates = dates;
        this.totals = totals;
    }
}
