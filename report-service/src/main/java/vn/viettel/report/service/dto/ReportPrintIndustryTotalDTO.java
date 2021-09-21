package vn.viettel.report.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportPrintIndustryTotalDTO {
    private String shopName;
    private String address;
    private String shopTel;
    private ShopDTO parentShop;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private LocalDateTime printDate;
    private ReturnGoodsDTO totalInfo;
    private List<ReportPrintOrderTotalDTO> data;

}
