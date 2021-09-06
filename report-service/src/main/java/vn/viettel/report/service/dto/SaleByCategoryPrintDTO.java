package vn.viettel.report.service.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "In báo cáo đổi hàng hòng")
public class SaleByCategoryPrintDTO {
    private String shopName;
    private String shopAddress;
    private String shopTel;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private LocalDateTime printDate;
    private Object[] total;
    private List<String> category;
    private List<Object[]> reportData;
}
