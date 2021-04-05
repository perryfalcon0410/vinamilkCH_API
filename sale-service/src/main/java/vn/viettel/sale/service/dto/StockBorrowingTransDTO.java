package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StockBorrowingTransDTO extends BaseDTO {
    private String transCode;
    private Date transDate;
    private Long shopId;
    private Integer type;
    private String note;
    private Long wareHouseTypeId;
    private Long stockBorrowingId;
    private Long fromShopId;
    private Long toShopId;
    private String redInvoiceNo;
    private String internalNumber;
    private Date adjustmentDate;
    private Float totalAmount;
    private Integer totalQuantity;
    private Integer status;
    private String createUser;
    private String updateUser;
}