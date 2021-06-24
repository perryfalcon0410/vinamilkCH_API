package vn.viettel.report.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Báo cáo xuất nhập tồn")
public class PrintInventoryDTO {
    private LocalDate fromDate;

    private LocalDate toDate;

    @ApiModelProperty(notes = "Ngày xuất báo cáo")
    private LocalDateTime reportDate = LocalDateTime.now();

    @ApiModelProperty(notes = "Cửa hàng")
    private ShopDTO shop;

    @ApiModelProperty(notes = "Tổng số lượng và thành tiền")
    ImportExportInventoryTotalDTO total;

    @ApiModelProperty(notes = "Danh sách ngành hàng")
    List<PrintInventoryCatDTO> cats;

    @JsonIgnore
    List<ImportExportInventoryDTO> products;

    public  PrintInventoryDTO(LocalDate fromDate, LocalDate toDate, ShopDTO shop) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.shop = shop;
    }

}
