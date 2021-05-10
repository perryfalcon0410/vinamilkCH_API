package vn.viettel.report.messaging;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseResponse;
import vn.viettel.report.service.dto.ShopImportDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShopImportResponse {
    private String fromDate;
    private String toDate;
    private String productIds;
    private Integer importType;
    private String internalNumber;
    private String toOrderDate;
    private String fromOrderDate;
    private List<ShopImportDTO> data;
}
