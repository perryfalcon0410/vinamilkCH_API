package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderReturnDTO extends BaseDTO {
    private String orderNumber;
    private String orderNumberRef;
    private String userName;
    private String customerNumber;
    private String customerName;
    private float amount;
    private float totalPromotion;
    private float total;
    private Date dateReturn;
    private Date orderDate;
}
