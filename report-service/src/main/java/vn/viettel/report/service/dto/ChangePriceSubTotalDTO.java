package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePriceSubTotalDTO {

    //Số hóa đơn
    private String redInvoiceNo;
    //Số PO
    private String poNumber;
    //mã giao dịch
    private String transCode;
    //Số nội bộ
    private String internalNumber;

    // ngày hóa đơn
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime orderDate;

    private Long totalQuantity;
    private Double totalPriceInput;
    private Double totalPriceOutput;


}
