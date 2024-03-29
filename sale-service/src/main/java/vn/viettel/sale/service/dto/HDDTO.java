package vn.viettel.sale.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class HDDTO {
    @ApiModelProperty(notes = "Mã cửa hàng")
    private String shopCode;
    @ApiModelProperty(notes = "Số po đơn hàng")
    private String invoiceNumber;
    @ApiModelProperty(notes = "Họ và tên khách hàng")
    private String fullName;
    @ApiModelProperty(notes = "Ngày báo cáo thuế")
    private LocalDateTime printDate;
    @ApiModelProperty(notes = "Mã số doanh nghiệp")
    private String taxCode;
    @ApiModelProperty(notes = "Tổng số tiền trươc thuế")
    private Double amountNotVat;
    @ApiModelProperty(notes = "Số hóa đơn")
    private String redInvoiceNum;
    @ApiModelProperty(notes = "Kho")
    private String wareHouse;
}
