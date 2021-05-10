package vn.viettel.report.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.report.service.dto.ShopImportDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopImportFilter {
    private String fromDate;
    private String toDate;
    private String productIds;
    private Integer importType;
    private String internalNumber;
    private String toOrderDate;
    private String fromOrderDate;
}
