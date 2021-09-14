package vn.viettel.common.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.messaging.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrinterConfigRequest extends BaseRequest {

    private String clientIp;

    private String billPrinterName;

    private String reportPrinterName;

    private String defaultPrinterName;

    private Boolean removeAccent;

    private  String userName;

}
