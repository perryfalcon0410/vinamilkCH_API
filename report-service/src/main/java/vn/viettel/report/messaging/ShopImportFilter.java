package vn.viettel.report.messaging;

import lombok.*;
import vn.viettel.report.service.dto.ShopImportDTO;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopImportFilter {
    private Date fromDate;
    private Date toDate;
    private String productCodes;
    private String importType;
    private String internalNumber;
    private Date fromOrderDate;
    private Date toOrderDate;
    private Long shopId;
}
