package vn.viettel.saleservice.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
