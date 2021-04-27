package vn.viettel.sale.service.dto;

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
public class ReportProductCatDTO {
    private String type;
    private Integer totalQuantity = 0;
    private Float totalPrice = 0F;
    private Float totalPriceNotVat = 0F;

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

    public Float addTotalTotalPrice(Float price) {
        if(price == null) return this.totalPrice;
        this.totalPrice += price;
        return this.totalPrice;
    }

    public Float addTotalPriceNotVar(Float priceNotVat) {
        if(priceNotVat == null) return this.totalPriceNotVat;
        this.totalPriceNotVat += priceNotVat;
        return this.totalPriceNotVat;
    }

}
