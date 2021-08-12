package vn.viettel.sale.service.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin sản phẩm")
public class ReportProductDTO {

    @ApiModelProperty(notes = "Mã Sản phẩm")
    private String productCode;

    @ApiModelProperty(notes = "Tên sản phẩm")
    private String productName;

    @ApiModelProperty(notes = "Đơn vị tính")
    private String unit;

    @ApiModelProperty(notes = "Số lượng")
    private Integer quantity;

    @ApiModelProperty(notes = "Giá sau thuế")
    private Double price = 0D;

    @ApiModelProperty(notes = "Tổng thành tiền giá sau thuế")
    private Double totalPrice = 0D;

    @ApiModelProperty(notes = "Giá trước thuế")
    private Double priceNotVat = 0D;

    @ApiModelProperty(notes = "Tổng thành tiền giá trước thuế")
    private Double totalPriceNotVat = 0D;

    public ReportProductDTO(String productCode, String productName) {
        this.productCode = productCode;
        this.productName = productName;
    }

    public void setPrice(Double price) {
        if(price != null) this.price = price;
    }

    public void setTotalPrice(Double totalPrice) {
        if(totalPrice != null) this.totalPrice = totalPrice;
    }

    public void setPriceNotVat(Double priceNotVat) {
        if(priceNotVat != null) this.priceNotVat = priceNotVat;
    }

    public void setTotalPriceNotVat(Double totalPriceNotVat) {
        if(totalPriceNotVat != null) this.totalPriceNotVat = totalPriceNotVat;
    }

}
