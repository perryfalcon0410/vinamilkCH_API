package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDataSearchDTO {
    @ApiModelProperty(notes = "id sản phẩm")
    private Long id;
    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;
    @ApiModelProperty(notes = "Mã sản phẩm")
    private String productCode;
    @ApiModelProperty(notes = "Đơn giá")
    private Double price;
    @ApiModelProperty(notes = "ĐVT")
    private String uom1;
    @ApiModelProperty(notes = "Ngành hàng")
    private String groupVat;
    @ApiModelProperty(notes = "Số lượng quy đổi")
    private Integer convfact;
    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;
    @ApiModelProperty(notes = "Thành tiền")
    private Double intoMoney;
    @ApiModelProperty(notes = "Thuế")
    private Double vat;
    @ApiModelProperty(notes = "Tiền thuế GTGT")
    private Double vatAmount;
    @ApiModelProperty(notes = "Ghi chú")
    private String note;


}

