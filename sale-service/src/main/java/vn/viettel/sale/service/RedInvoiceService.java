package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.sale.RedInvoice;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.service.dto.RedInvoiceDTO;

import java.util.Date;
import java.util.List;

public interface RedInvoiceService extends BaseService {
    Response<Page<RedInvoiceDTO>> getAll(RedInvoiceFilter redInvoiceFilter, Pageable pageable);
}
