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
@ApiModel(description = "Thông tin sản phẩm combo trong xuất nhập combo")
public class ComboProductTransComboDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã combo")
    private String productCode;

    @ApiModelProperty(notes = "Tên combo")
    private String productName;

    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private Double productPrice;

}
