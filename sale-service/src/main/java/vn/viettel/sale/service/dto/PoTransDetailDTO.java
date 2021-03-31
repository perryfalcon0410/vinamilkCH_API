package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import javax.persistence.Column;
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
    private Integer quantity;
    private Float price;
    private Float priceNotVat;
    private Float amount;
    private Float amountNotVat;
    private String export;
    private String remaining;
}
