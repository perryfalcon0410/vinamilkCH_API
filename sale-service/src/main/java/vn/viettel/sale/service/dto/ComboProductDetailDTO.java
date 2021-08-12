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

    @ApiModelProperty(notes = "Số lượng sản phẩm quy đổi thuộc combo")
    private Integer numProduct;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Hệ số quy đổi")
    private Integer factor;

    @ApiModelProperty(notes = "Giá của sản phẩm")
    private Double productPrice;

    @ApiModelProperty(notes = "Giá của sản phẩm chưa thuế")
    private Double productPriceNotVat;

    @ApiModelProperty(notes = "Id sản phẩm combo")
    private Long refProductId;

    @ApiModelProperty(notes = "Trạng thái")
    private Integer status;

    //constructor dùng để lấy repository -> ko được sửa, xóa
    public ComboProductDetailDTO(Long comboProductId, Long refProductId, String comboProductCode, Integer numProduct, Long productId,
                                 String productCode, String productName,/* Double productPrice, Double productPriceNotVat,*/ Integer factor){
        this.comboProductId = comboProductId;
        this.comboProductCode = comboProductCode;
        this.productId = productId;
//        this.productPrice = productPrice;
        this.numProduct = numProduct;
        this.productCode = productCode;
        this.productName = productName;
//        this.productPriceNotVat = productPriceNotVat;
        this.factor = factor;
        this.refProductId = refProductId;
    }
}
