package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "Thông tin sản phẩm chọn mua")
public class OrderProductDTO extends BaseDTO {
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Giá sản phẩm")
    private Float price;

    @ApiModelProperty(notes = "Số lượng tồn kho hiện tại")
    private Integer stockTotal;

    @ApiModelProperty(notes = "Trạng thái sản phẩm")
    private Integer status;

    @ApiModelProperty(notes = "Đơn vị tính")
    private String uom1;

    @ApiModelProperty(notes = "Sản phẩm combo")
    private Boolean isCombo;

    @ApiModelProperty(notes = "Id sản phẩm combo tương ứng")
    private Long comboProductId;

    @ApiModelProperty(notes = "Tên hình")
    private String image;
}

