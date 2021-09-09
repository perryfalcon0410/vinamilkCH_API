package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApiModel(description = "Danh sách sản phẩm theo ngành hàng")
public class PromotionProductCatDTO {
    @JsonIgnore
    private Long productCatId;
    @ApiModelProperty(notes = "Mã ngành hàng")
    private String productCatName;
    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity = 0;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private Double totalPrice = 0D;

    @ApiModelProperty(notes = "Danh sách sản phẩm thuộc ngành hàng")
    List<PromotionProductDTO> productCats = new ArrayList<>();

    public PromotionProductCatDTO( String productCatName) {
        this.productCatName = productCatName;
    }

    public void addProduct(PromotionProductDTO productDTO) {
        this.productCats.add(productDTO);
    }

    public Integer addTotalQuantity(Integer quantity) {
        this.totalQuantity += quantity;
        return this.totalQuantity;
    }

    public Double addTotalTotalPrice(Double price) {
        this.totalPrice += price;
        return this.totalPrice;
    }

}
