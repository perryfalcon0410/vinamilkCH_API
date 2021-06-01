package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PoTransDetailDTO extends BaseDTO {
    private Long transId;
    private Date transDate;
    private Long shopId;
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    private Integer quantity;
    private Double price;
    private Double priceNotVat;
    private Double amount;
    private Double amountNotVat;
    private Double totalPrice;
    private Integer export;
    private String soNo;
}
