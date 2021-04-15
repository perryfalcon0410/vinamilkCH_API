package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PoDetailDTO extends BaseDTO {
    private Long poId;
    private Date orderDate;
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private String shopContact;
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    private Integer quantity;
    private Float price;
    private Float priceNotVat;
    private Float amountNotVat;
    private Float vat;
    private Float totalPrice;
    private String soNo;
}
