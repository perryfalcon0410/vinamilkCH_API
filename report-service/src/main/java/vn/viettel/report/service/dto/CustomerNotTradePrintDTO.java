package vn.viettel.report.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.dto.ShopDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomerNotTradePrintDTO {
    private String shopName;
    private String address;
    private String shopTel;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private LocalDateTime printDate;
    List<CustomerReportDTO> data;
    private ShopDTO parentShop;

}
