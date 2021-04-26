package vn.viettel.sale.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnlineOrderDTO extends BaseDTO {

    private String orderNumber;

    private String orderInfo;

    private String discountCode;

    private Float discountValue;

    private int quantity = 0;

    private float totalPrice = 0;

    private List<OrderProductDTO> products;

    private CustomerDTO customer;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void addTotalPrice(float totalPrice) {
        this.totalPrice += totalPrice;
    }
}