package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class ProductDataSearchDTO extends BaseDTO {
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Đơn giá")
    private Float price;
    @ApiModelProperty(notes = "ĐVT")
    private String uom1;
    @ApiModelProperty(notes = "Ngành hàng")
    private String groupVat;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Thành tiền")
    private Float intoMoney;
    @ApiModelProperty(notes = "Thuế")
    private Float vat;
    @ApiModelProperty(notes = "Tiền thuế GTGT")
    private Float vatAmount;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;


}

