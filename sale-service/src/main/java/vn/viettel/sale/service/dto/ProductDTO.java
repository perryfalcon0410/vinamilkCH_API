package vn.viettel.sale.service.dto;

import liquibase.pro.packaged.F;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO extends BaseDTO {

    private String productName;

    private String productCode;

    private Float price;

    private Integer stockTotal;

    private Integer status;

    private String uom1;

    private Boolean isCombo;

    private Long comboProductId;

    private String industry;

    private Integer quantity;

    private Float intoMoney;

    private Float vat;

    private Float vatAmount;

    private String note;



}

