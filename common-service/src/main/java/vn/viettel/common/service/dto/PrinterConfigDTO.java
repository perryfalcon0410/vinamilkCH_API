package vn.viettel.common.service.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.service.dto.BaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrinterConfigDTO extends BaseDTO {

    private String clientIp;

    private String billPrinterName;

    private String reportPrinterName;

    private String defaultPrinterName;
}
