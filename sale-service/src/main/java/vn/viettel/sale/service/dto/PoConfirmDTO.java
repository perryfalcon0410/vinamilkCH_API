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
public class PoConfirmDTO extends BaseDTO {
    private String poCode;
    private Long shopId;
    private String poNumber;
    private String internalNumber;
    private String saleOrderNumber;
    private Date orderDate;
    private Date cancelDate;
    private Integer totalQuantity;
    private Float totalAmount;
    private Integer status;
    private Integer cancelReason;
    private String cancelUser;
    private Date denyDate;
    private Integer denyReason;
    private String denyUser;
    private Date importDate;
    private String importCode;
    private String importUser;
    private Long wareHouseTypeId;
}
