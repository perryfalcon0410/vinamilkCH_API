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
public class RedInvoiceDetailDTO extends  BaseDTO{
    private Long redInvoiceId;
    private Long shopId;
    private Date printDate;
    private Long productId;
    private Integer quantity;
    private Float price;
    private Float priceNotVat;
    private Float amountNotVat;
    private Float amount;
    private String note;

}
