package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Báo cáo xuất nhập tồn")
public class PrintInventoryDTO {
    private Date fromDate;

    private Date toDate;

    @ApiModelProperty(notes = "Ngày xuất báo cáo")
    private Date reportDate = new Date();

    @ApiModelProperty(notes = "Cửa hàng")
    private ShopDTO shop;

    @ApiModelProperty(notes = "Tổng số lượng và thành tiền")
    ImportExportInventoryTotalDTO total;

    @ApiModelProperty(notes = "Danh sách sản phẩm")
    List<ImportExportInventoryDTO> products;

    public  PrintInventoryDTO(Date fromDate, Date toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

}
