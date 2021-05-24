package vn.viettel.sale.service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.sale.entities.Product;

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
    private String redInvoiceNumber;
    private Date printDate;
    private String officeWorking;
    private String officeAddress;
    private String taxCode;
    private Float totalQuantity;
    private Float amountTotal;
    private Integer paymentType;
    private String note;
    private String buyerName;
    private List<ProductDataDTO> productDataDTOS;
}