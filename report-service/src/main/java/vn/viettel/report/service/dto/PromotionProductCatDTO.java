package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PromotionProductCatDTO {
    @JsonIgnore
    private Long productCatId;
    private String productCatName;
    private Integer totalQuantity = 0;
    private Float totalPrice = 0F;

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

    public Float addTotalTotalPrice(Float price) {
        if(price == null) return this.totalPrice;
        this.totalPrice += price;
        return this.totalPrice;
    }

}
