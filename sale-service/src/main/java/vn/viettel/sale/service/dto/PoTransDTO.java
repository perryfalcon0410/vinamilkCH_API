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
public class PoTransDTO extends BaseDTO {

    private String transCode;

    private LocalDateTime transDate;

    private Long shopId;

    private Long wareHouseTypeId;

    private String wareHouseTypeName;

    private Integer type;

    private Integer receiptType;

    private String redInvoiceNo;

    private String pocoNumber;

    private String internalNumber;

    private String poNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

    private Float discountAmount;

    private String discountDescr;

    private String note;

    private Boolean isDebit;

    private Integer status;

    private Float totalAmount;

    private Long poId;

    private Long fromTransId;

    private Integer numSku;

    private Integer totalQuantity;

    private String returnNote;
}
