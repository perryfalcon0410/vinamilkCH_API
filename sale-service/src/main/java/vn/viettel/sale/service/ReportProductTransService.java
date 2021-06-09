package vn.viettel.sale.service;

import io.swagger.models.auth.In;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.ReportProductTransDTO;

public interface ReportProductTransService extends BaseService {

    Response<ReportProductTransDTO> getInvoice(Long shopId, Long id, Integer receiptType);
}
