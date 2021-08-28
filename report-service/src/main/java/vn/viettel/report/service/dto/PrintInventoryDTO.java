package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Báo cáo xuất nhập tồn")
public class PrintInventoryDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime fromDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime toDate;

    @ApiModelProperty(notes = "Ngày xuất báo cáo")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime reportDate = LocalDateTime.now();

    @ApiModelProperty(notes = "Cửa hàng")
    private ShopDTO shop;

    @ApiModelProperty(notes = "Tổng số lượng và thành tiền")
    ImportExportInventoryTotalDTO total;

    @ApiModelProperty(notes = "Danh sách ngành hàng")
    List<PrintInventoryCatDTO> cats;

    @JsonIgnore
    List<ImportExportInventoryDTO> products;

    public  PrintInventoryDTO(LocalDateTime fromDate, LocalDateTime toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

}
