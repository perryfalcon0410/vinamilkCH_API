package vn.viettel.sale.service.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.Constants;
import vn.viettel.sale.entities.Product;

import java.time.LocalDateTime;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime printDate;
    private String officeWorking;
    private String officeAddress;
    private String taxCode;
    private Float totalQuantity;
    private Float totalMoney;
    private Integer paymentType;
    private String noteRedInvoice;
    private String buyerName;
    private List<ProductDataDTO> productDataDTOS;
}