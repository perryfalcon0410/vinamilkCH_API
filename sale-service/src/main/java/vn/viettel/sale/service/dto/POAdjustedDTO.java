package vn.viettel.sale.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class POAdjustedDTO extends BaseDTO{

    private String poAdjustedNumber;

    private Long shopId;

    private Timestamp poDate;

    private String poNote;

    private Integer poType;

    private Integer status;
}
