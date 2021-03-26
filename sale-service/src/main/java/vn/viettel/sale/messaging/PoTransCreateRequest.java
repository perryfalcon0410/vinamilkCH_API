package vn.viettel.sale.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PoTransCreateRequest {
    private Long poConfirmId;
    private Integer importType;
    ////////////////////////////////////////////
    private String transCode;
    private Date transDate;
    private Long shopId;
    private Long wareHouseTypeId;
    private Integer type;
    private String redInvoiceNo;
    private String pocoNumber;
    private String internalNumber;
    private String poNumber;
    private Date orderDate;
    private Float discountAmount;
    private String discountDescr;
    private String note;
    private Boolean isDebit;
    private Integer status;
    private Long poId;
    private Long fromTransId;
    private Integer numSku;
    private Integer totalQuantity;
    private String createUser;
    private String updateUser;
}
