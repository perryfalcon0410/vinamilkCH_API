package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderReturnDTO {
    private Date orderDate;
    private String userName;
    private String customerName;
    private float totalPaidReturn;
}
