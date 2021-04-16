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
    private Float price;
    private Float priceNotVat;
    private Float amount;
    private Float amountNotVat;
    private Float totalPrice;
    private Integer export;
    private String soNo;
}
