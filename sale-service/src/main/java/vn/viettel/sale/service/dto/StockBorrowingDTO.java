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
public class StockBorrowingDTO extends BaseDTO {
    private String poBorrowCode;
    private Long shopId;
    private Long toShopId;
    private Date borrowDate;
    private Integer status;
    private Long wareHouseTypeId;
    private Integer totalQuantity;
    private Float totalAmount;
}
