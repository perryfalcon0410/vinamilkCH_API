package vn.viettel.sale.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PoConfirmDTO extends BaseDTO {
    private Long id;
    private String poCode;
    private Long shopId;
    private String poNumber;
    private String poCoNumber;
    private String internalNumber;
    private String saleOrderNumber;
    private LocalDateTime orderDate;
    private LocalDateTime cancelDate;
    private Integer totalQuantity;
    private Float totalAmount;
    private Integer status;
    private Integer cancelReason;
    private String cancelUser;
    private LocalDateTime denyDate;
    private Integer denyReason;
    private String denyUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime importDate;
    private String importCode;
    private String importUser;
    private Long wareHouseTypeId;
    private String wareHouseTypeName;
}
