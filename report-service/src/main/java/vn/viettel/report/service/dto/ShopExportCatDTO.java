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
@ApiModel(description = "Chi tiết ngành hàng trong đơn")
public class ShopExportCatDTO {

    @ApiModelProperty(notes = "Tên ngành hàng")
    private String catName;

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;

    @ApiModelProperty(notes = "Tổng giá trước thuế - đối với PO thì lấy giá này")
    private Double totalPriceNotVat;

    @ApiModelProperty(notes = "Tổng giá sau thuế - đối với nhập điều chỉnh và vay mượn lấy giá này")
    private Double totalPriceVat;

    @ApiModelProperty(notes = "Danh sách sản phẩm")
    List<ShopExportDTO> products;

}
