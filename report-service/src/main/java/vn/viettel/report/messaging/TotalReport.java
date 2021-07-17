package vn.viettel.report.messaging;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TotalReport {

    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;
    @ApiModelProperty(notes = "Tổng số lượng packet")
    private Integer totalPacketQuantity;
    @ApiModelProperty(notes = "Tổng số lượng lẻ")
    private Integer totalUnitQuantity;
    @ApiModelProperty(notes = "Tổng Thành tiền không thuế")
    private Float totalAmountNotVat;
    @ApiModelProperty(notes = "Tổng Thành tiền có thuế")
    private Float totalAmountVat;

//    @ApiModelProperty(notes = "Tổng số tiền sau thuế")
//    private Float totalPrice;
//    @ApiModelProperty(notes = "Tổng số lượng")
//    private Integer totalQuantity;
//    @ApiModelProperty(notes = "Tổng số lượng packet")
//    private Integer totalPacketQuantity;
//    @ApiModelProperty(notes = "Tổng số lượng lẻ")
//    private Integer totalUnitQuantity;
//    @ApiModelProperty(notes = "Tổng Thành tiền không thuế")
//    private Float totalAmountNotVat;
//    @ApiModelProperty(notes = "Tổng Thành tiền có thuế")
//    private Float totalAmount;
//    @ApiModelProperty(notes = "Tên shop")
//    private String shopName;
//    @ApiModelProperty(notes = "Địa chỉ shop")
//    private String shopAddress;
//    @ApiModelProperty(notes = "Số điện thoại shop")
//    private String shopPhone;
//    @ApiModelProperty(notes = "Từ ngày")
//    private LocalDate toDate;
//    @ApiModelProperty(notes = "Đến ngày")
//    private LocalDate fromDate;
//    @ApiModelProperty(notes = "Đến ngày")
//    private LocalDateTime printDate;
}
