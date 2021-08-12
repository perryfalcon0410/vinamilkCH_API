package vn.viettel.sale.service;

import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;

import java.util.List;

public interface RedInvoiceDetailService extends BaseService {
    List<RedInvoiceDetailDTO> getRedInvoiceDetailByRedInvoiceId(Long id);
}
