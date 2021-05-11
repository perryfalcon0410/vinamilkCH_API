package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin ngành hàng ngành, hàng con... của sản phẩm")
public class ProductInfoDTO extends BaseDTO {

    private Long id;

    @ApiModelProperty(notes = "Mã thông tin")
    private String productInfoCode;

    @ApiModelProperty(notes = "Tên thông tin")
    private String productInfoName;

    @ApiModelProperty(notes = "Ghi chú")
    private String description;

    @ApiModelProperty(notes = "Loại")
    private Integer type;

    @ApiModelProperty(notes = "Trạng thái")
    private Integer status;
}
