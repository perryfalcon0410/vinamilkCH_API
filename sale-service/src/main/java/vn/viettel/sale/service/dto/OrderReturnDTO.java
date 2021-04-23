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
    private String orderReturnNumber;
    private String orderNumber;
    private String userName;
    private String customerNumber;
    private String customerName;
    private float amount;
    private float discount;
    private float total;
    private Date dateReturn;
//    private Date orderDate;
}
