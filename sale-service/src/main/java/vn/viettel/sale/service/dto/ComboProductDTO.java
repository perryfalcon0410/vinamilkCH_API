package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin sản phẩm combo")
public class ComboProductDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã sản phẩm combo")
    private String productCode;

    @ApiModelProperty(notes = "Tên sản phẩm combo")
    private String productName;

    @ApiModelProperty(notes = "Id sản phẩm tương ứng")
    private Long refProductId;

    @ApiModelProperty(notes = "Số lượng sản phẩm quy đổi thuộc combo")
    private Integer numProduct;

    @ApiModelProperty(notes = "Giá của sản phẩm")
    private Double productPrice;

    @ApiModelProperty(notes = "Trạng thái của sản phẩm")
    private Integer status;

    @ApiModelProperty(notes = "Danh sách sản phẩm trong sản phẩm combo")
    List<ComboProductDetailDTO> details;
}
