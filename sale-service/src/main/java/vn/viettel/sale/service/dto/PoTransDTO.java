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
public class PoTransDTO extends BaseDTO {

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
