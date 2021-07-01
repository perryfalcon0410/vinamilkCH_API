package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PrintShopImportTotalDTO {
    @ApiModelProperty(notes = "Tổng số tiền sau thuế")
    private Float totalPrice;
    @ApiModelProperty(notes = "Tổng số lượng")
    private Integer totalQuantity;
    @ApiModelProperty(notes = "Tổng số lượng packet")
    private Integer totalPacketQuantity;
    @ApiModelProperty(notes = "Tổng số lượng lẻ")
    private Integer totalUnitQuantity;
    @ApiModelProperty(notes = "Tổng Thành tiền không thuế")
    private Float totalAmountNotVat;
    @ApiModelProperty(notes = "Tổng Thành tiền có thuế")
    private Float totalAmount;
    @ApiModelProperty(notes = "Tên shop")
    private String shopName;
    @ApiModelProperty(notes = "Địa chỉ shop")
    private String shopAddress;
    @ApiModelProperty(notes = "Số điện thoại shop")
    private String shopPhone;
    @ApiModelProperty(notes = "Từ ngày")
    private LocalDateTime toDate;
    @ApiModelProperty(notes = "Đến ngày")
    private LocalDateTime fromDate;
    @ApiModelProperty(notes = "Đến ngày")
    private LocalDateTime printDate;
}
