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
    private Double price;

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

    @ApiModelProperty(notes = "Tên sản phẩm in hoa, bỏ dấu")
    private String nameText;

    @ApiModelProperty(notes = "Mã vạch sản phẩm")
    private String barCode;

    public OrderProductDTO(Long id, String productName, String productCode, Double price, Integer stockTotal, Integer status, String uom1,
                           Boolean isCombo, Long comboProductId, String image, String nameText, String barCode){
        this.setId(id);
        this.productName = productName;
        this.productCode = productCode;
        this.price = price;
        this.stockTotal = stockTotal;
        this.status = status;
        this.uom1 = uom1;
        this.isCombo = isCombo;
        this.comboProductId = comboProductId;
        this.image = image;
        this.nameText = nameText;
        this.barCode = barCode;
    }

    public String getImage(){
        if(image != null)
            image = image.substring(image.lastIndexOf('/')+1).trim();
        return image;
    }
}

