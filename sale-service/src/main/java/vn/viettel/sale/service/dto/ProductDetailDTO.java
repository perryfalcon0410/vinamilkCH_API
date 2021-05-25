package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO extends BaseDTO {
    @ApiModelProperty(notes = "Mã đơn hàng")
    private String orderNumber;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "ĐVT1")
    private String uom1;
    @ApiModelProperty(notes = "ĐVT2")
    private String uom2;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Đơn giá")
    private Float unitPrice;
    @ApiModelProperty(notes = "Thành tiền")
    private Float intoMoney;
    @ApiModelProperty(notes = "Ngành hàng")
    private String groupVat;
}
