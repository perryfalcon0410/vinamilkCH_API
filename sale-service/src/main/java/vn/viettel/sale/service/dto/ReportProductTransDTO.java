package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông khuyến in hóa đơn nhập - xuất hàng")
public class ReportProductTransDTO extends BaseDTO {

    @ApiModelProperty(notes = "Thông tin cử hàng")
    private ShopDTO shop;

    @ApiModelProperty(notes = "Thông tin đơn hàng")
    private ReportProductTransDetailDTO info;

    @ApiModelProperty(notes = "Danh sách sản phẩm bán")
    private List<ReportProductCatDTO> saleProducts;

    @ApiModelProperty(notes = "Danh sách sản phẩm mua")
    private List<ReportProductCatDTO> promotionProducts;

}
