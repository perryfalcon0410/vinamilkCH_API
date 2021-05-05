package vn.viettel.sale.service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedInvoiceNewDataDTO extends BaseDTO {
    private Long shopId;
    private Long customerId;
    private List<Long> saleOrderId;
    private String customerCode;
    private String customerName;
    private String redInvoiceNumber;
    private Date printDate;
    private String officeWorking;
    private String officeAddress;
    private String taxCode;
    private String createUser;
    private Integer paymentType;
    private Float totalQuantity;
    private Float amount;
    private String note;

    private List<ProductDataDTO> productDataDTOS;
}