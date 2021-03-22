package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class PoBorrowDetailDTO extends BaseDTO {

    private Long poBorrowId;


    private String poBorrowDetailNumber;


    private String productCode;


    private String productName;


    private Float productPrice;


    private Integer quantity;


    private Float priceTotal;


    private Integer isFreeItem;

    private String unit;
}
