package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Thông tin ngành hàng")
public class ReportProductCatDTO {
    @ApiModelProperty(notes = "Tên ngành hàng")
    private String type;

    @ApiModelProperty(notes = "Tổng số lượng sản phẩm")
    private Integer totalQuantity = 0;

    @ApiModelProperty(notes = "Tổng tiền sau thuế - có VAT")
    private Double totalPrice = 0D;

    @ApiModelProperty(notes = "Tổng tiền trước thuế - chưa có VAT")
    private Double totalPriceNotVat = 0D;

    @ApiModelProperty(notes = "Danh sách sản phẩm")
    List<ReportProductDTO> products = new ArrayList<>();

    public ReportProductCatDTO(String type) {
        this.type = type;
    }

    public void addProduct(ReportProductDTO productDTO) {
        this.products.add(productDTO);
    }

    public Integer addTotalQuantity(Integer quantity) {
        this.totalQuantity += quantity;
        return this.totalQuantity;
    }

    public Double addTotalPrice(Double price) {
        this.totalPrice += price;
        return this.totalPrice;
    }

    public Double addTotalPriceNotVar(Double priceNotVat) {
        this.totalPriceNotVat += priceNotVat;
        return this.totalPriceNotVat;
    }

}
