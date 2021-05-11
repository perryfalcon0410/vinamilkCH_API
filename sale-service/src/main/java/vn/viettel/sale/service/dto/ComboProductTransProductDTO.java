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
@ApiModel(description = "Thông tin sản phẩm thuộc sản phẩm combo")
public class ComboProductTransProductDTO extends BaseDTO {

    @ApiModelProperty(notes = "Mã sản phẩm combo")
    private String comboProductCode;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Tê sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Hệ số quy đổi")
    private Integer factor;

    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;

    @ApiModelProperty(notes = "Giá")
    private Float price;

}
