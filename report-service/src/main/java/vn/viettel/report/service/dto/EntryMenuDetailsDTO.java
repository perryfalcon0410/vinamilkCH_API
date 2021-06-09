package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Thông tin bảng kê chi tiết đơn nhập hàng")
@Entity
public class EntryMenuDetailsDTO {

    @Id
    @Column(name = "ID")
    private Long id;
    @ApiModelProperty(notes = "Số PO")
    @Column(name = "PO_NUMBER")
    private String poNumber;

    @ApiModelProperty(notes = "Số nội bộ")
    @Column(name = "INTERNAL_NUMBER")
    private String internalNumber;

    @ApiModelProperty(notes = "Số hóa đơn")
    @Column(name = "RED_INVOICE_NO")
    private String redInvoiceNo;

    @ApiModelProperty(notes = "ngày hóa đơn")
    @Column(name = "BILL_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="America/New_York")
    private LocalDateTime billDate;

    @ApiModelProperty(notes = "Ngày thanh toán")
    @Column(name = "DATE_OF_PAYMENT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="America/New_York")
    private LocalDateTime dateOfPayment;

    @ApiModelProperty(notes = "Số tiền")
    @Column(name = "AMOUNT")
    private Float amount;

    @ApiModelProperty(notes = "Tổng tiền")
    @Column(name = "TOTAL_AMOUNT")
    private Float totalAmount;

    @ApiModelProperty(notes = "Mã trả hàng")
    @Column(name = "PROMOTIONAL_ORDERS")
    private String promotionalOrders;

}
