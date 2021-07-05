package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrintShopImportFilterDTO {
    private String shopName;
    private String address;
    private String shopTel;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private LocalDateTime printDate;
    private PrintShopImportDTO totalInfo;
    private List<PrintShopImportTotalDTO> dataType;
}
