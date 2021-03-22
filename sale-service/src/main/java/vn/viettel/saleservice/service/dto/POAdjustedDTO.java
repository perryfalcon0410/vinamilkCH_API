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
public class POAdjustedDTO extends BaseDTO{

    private String poAdjustedNumber;

    private Long shopId;

    private Timestamp poDate;

    private String poNote;

    private Integer poType;

    private Integer status;
}
