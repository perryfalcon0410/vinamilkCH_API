package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RedInvoiceDTO extends BaseDTO {
    private String invoiceNumber;
    private Long shopId;
    private String officeWorking;
    private String officeAddress;
    private String taxCode;
    private Date printDate;
    private String note;
    private Long customerId;
    private Integer paymentType;
    private String orderNumbers;

    @ApiModelProperty(notes = "Tổng số lượng")
    private Float totalQuantity;
    @ApiModelProperty(notes = "Tổng thành tiền")
    private Float totalMoney;
    @ApiModelProperty(notes = "Tổng thành tiền không thuế")
    private Float amountNotVat;
    @ApiModelProperty(notes = "Tổng tiền thuế GTGT")
    private Float amountGTGT;



}
