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
@ApiModel(description = "In báo cáo xuất hàng")
public class PrintShopExportDTO {
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

    @ApiModelProperty(notes = "Danh sách nhập hàng PO")
    private PrintShopExportTotalDTO expPO;

    @ApiModelProperty(notes = "Danh sách nhập điều chỉnh")
    private PrintShopExportTotalDTO expAdjust;

    @ApiModelProperty(notes = "Danh sách nhập vay mượng")
    private PrintShopExportTotalDTO expBorrow;


    public PrintShopExportDTO(String shopName, String address, String shopTel) {
        this.shopName = shopName;
        this.address = address;
        this.shopTel = shopTel;
    }


}
