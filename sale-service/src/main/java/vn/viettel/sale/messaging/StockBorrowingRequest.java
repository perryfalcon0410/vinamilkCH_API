package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StockBorrowingRequest {
    private Integer importType;
    private Long borrowingId;
    //////////////////////////////////////////////////////
    private String transCode;
    private Date transDate;
    private Long shopId;
    private Integer type;
    private String note;
    private Long wareHouseTypeId;
    private Long stockBorrowingId;
    private Long fromShopId;
    private Long toShopId;
    private Integer status;
    private String createUser;
    private String updateUser;
}
