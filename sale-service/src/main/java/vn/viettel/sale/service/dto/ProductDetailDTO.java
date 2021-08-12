package vn.viettel.sale.service.dto;

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
    private Double unitPrice;
    @ApiModelProperty(notes = "Thành tiền")
    private Double intoMoney;

    @ApiModelProperty(notes = "Ngành hàng")
    private String groupVat;

    //Không được sửa hàm này vì được sử dụng ở ProductRepository
    public ProductDetailDTO(Long id, String orderNumber, String productCode, String productName, String uom1, String uom2, String groupVat, Integer quantity,
                            Double unitPrice, Double intoMoney ){
        this.setId(id);
        this.orderNumber = orderNumber;
        this.productName = productName;
        this.productCode = productCode;
        this.uom1 = uom1;
        this.uom2 = uom2;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.intoMoney = intoMoney;
        this.groupVat = groupVat;
    }
}
