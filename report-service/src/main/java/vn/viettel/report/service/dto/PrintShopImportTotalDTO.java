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
public class PrintShopImportTotalDTO {

//    private String type;

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Tổng giá trước thuế - đối với PO thì lấy giá này")
    private Double totalPriceNotVat;

    @ApiModelProperty(notes = "Tổng giá sau thuế - đối với nhập điều chỉnh và vay mượn lấy giá này")
    private Double totalPriceVat;


    @ApiModelProperty(notes = "Danh sách đơn hàng")
    private List<OrderImportDTO> orderImports  = new ArrayList<>();

    public void addOrderImport(OrderImportDTO orderImport) {
        this.orderImports.add(orderImport);
    }
}
