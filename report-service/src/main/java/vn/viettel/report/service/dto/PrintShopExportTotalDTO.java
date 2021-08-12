package vn.viettel.report.service.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Chi tiết loại đơn hàng")
public class PrintShopExportTotalDTO {

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Tổng giá trước thuế - đối với PO thì lấy giá này")
    private Double totalPriceNotVat;

    @ApiModelProperty(notes = "Tổng giá sau thuế - đối với xuất điều chỉnh và vay mượn lấy giá này")
    private Double totalPriceVat;

    @ApiModelProperty(notes = "Danh sách đơn hàng")
    private List<OrderExportDTO> orderImports  = new ArrayList<>();

    public void addOrderImport(OrderExportDTO orderExport) {
        this.orderImports.add(orderExport);
    }

}
