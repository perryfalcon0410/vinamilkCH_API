package vn.viettel.common.service;

import vn.viettel.common.messaging.PrinterConfigRequest;
import vn.viettel.common.service.dto.PrinterConfigDTO;
import vn.viettel.core.service.BaseService;

public interface PrinterConfigService extends BaseService {

    PrinterConfigDTO create(PrinterConfigRequest request);

    PrinterConfigDTO update(Long id, PrinterConfigRequest request);

    PrinterConfigDTO getPrinter(String clientIp);

}
