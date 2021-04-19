package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductsDTO {
    private int quantity = 0;

    private float totalPrice = 0;

    private List<OrderProductDTO> products;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void addTotalPrice(float totalPrice) {
        this.totalPrice += totalPrice;
    }
}
