package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "In báo cáo nhập hàng")
public class PrintShopImportDTO {

    @ApiModelProperty(notes = "Tên Shop")
    private String shopName;

    @ApiModelProperty(notes = "Địa chỉ shop")
    private String address;

    @ApiModelProperty(notes = "Số điện thoại")
    private String shopTel;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    @ApiModelProperty(notes = "Ngày in")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime printDate = LocalDateTime.now();

    @ApiModelProperty(notes = "Tổng số lượng")
    private Long totalQuantity;

    @ApiModelProperty(notes = "Tổng thành tiền")
    private Double totalAmount;

    @ApiModelProperty(notes = "Tổng tiền thuế")
    private Double totalVat;

    @ApiModelProperty(notes = "Danh sách nhập hàng PO")
    private PrintShopImportTotalDTO impPO;

    @ApiModelProperty(notes = "Danh sách nhập điều chỉnh")
    private PrintShopImportTotalDTO impAdjust;

    @ApiModelProperty(notes = "Danh sách nhập vay mượng")
    private PrintShopImportTotalDTO impBorrow;

    @ApiModelProperty(notes = "Danh sách xuất trả PO")
    private PrintShopImportTotalDTO expPO;

    public PrintShopImportDTO(String shopName, String address, String shopTel) {
        this.shopName = shopName;
        this.address = address;
        this.shopTel = shopTel;
    }

}
