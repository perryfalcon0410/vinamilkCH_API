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
public class ProductOrderDataDTO {
    private Integer orderType;
    private Integer quantity = 0;
    private Double totalPrice = 0.0;
    private Double totalPriceNotVAT = 0.0;

    List<ProductOrderDetailDataDTO> products;

    public ProductOrderDataDTO(Integer orderType) {
        this.orderType = orderType;
    }

    public ProductOrderDataDTO addQuantity(Integer quantity) {
        this.quantity += quantity;
        return this;
    }

    public ProductOrderDataDTO addTotalPrice(Double totalPrice) {
        this.totalPrice += totalPrice;
        return this;
    }

    public ProductOrderDataDTO addTotalPriceNotVAT(Double totalPriceNotVAT) {
        this.totalPriceNotVAT += totalPriceNotVAT;
        return this;
    }

    public ProductOrderDataDTO addProduct(ProductOrderDetailDataDTO product) {
        if(products == null) products = new ArrayList<>();
        products.add(product);
        return this;
    }


}
