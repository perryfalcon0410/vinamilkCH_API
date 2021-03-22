package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class POBorrowDTO extends BaseDTO {


    private String poBorrowNumber;
    private Long shopId;
    private Long toShopId;
    private Integer poType;
    private Timestamp poDate;
    private String poNote;
    private Long wareHouseTypeId;
    private Integer status;
}
