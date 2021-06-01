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
    private Double price;
    private Double priceNotVat;
    private Double amountNotVat;
    private Double vat;
    private Double totalPrice;
    private String soNo;
}
