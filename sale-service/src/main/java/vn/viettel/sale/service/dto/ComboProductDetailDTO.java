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
@ApiModel(description = "Thông tin chi tiết sản phẩm trong combo")
public class ComboProductDetailDTO extends BaseDTO {
    @ApiModelProperty(notes = "Id sản phẩm combo")
    private Long comboProductId;

    @ApiModelProperty(notes = "Id sản phẩm")
    private Long productId;

    @ApiModelProperty(notes = "Mã sản phẩm combo")
    private String comboProductCode;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Hệ số quy đổi")
    private Float factor;

    @ApiModelProperty(notes = "Giá của sản phẩm")
    private Double productPrice;

    @ApiModelProperty(notes = "Trạng thái")
    private Integer status;

}
