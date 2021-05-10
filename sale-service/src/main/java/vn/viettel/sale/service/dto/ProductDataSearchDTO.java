package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class ProductDataSearchDTO extends BaseDTO {

    private String productName;

    private String productCode;

    private Float price;

    private String uom1;

    private String groupVat;

    private Integer quantity;

    private Float intoMoney;

    private Float vat;

    private Float vatAmount;

    private String note;



}

