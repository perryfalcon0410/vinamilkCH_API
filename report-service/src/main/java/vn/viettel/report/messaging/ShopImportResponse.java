package vn.viettel.report.messaging;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseResponse;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShopImportResponse {
    private String fromDate;
    private String toDate;
    private ShopImportTotalDTO total;
    private List<ShopImportDTO> data;
}
