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
public class StockAdjustmentTransDTO extends BaseDTO {
    private String transCode;
    private Date transDate;
    private Long shopId;
    private String note;
    private Integer type;
    private Integer ReceiptType;
    private Integer status;
    private Long wareHouseTypeId;
    private String wareHouseTypeName;
    private Long adjustmentId;
    private String redInvoiceNo;
    private String internalNumber;
    private Date adjustmentDate;
    private Float totalAmount;
    private Integer totalQuantity;
    private Long reasonId;
}
