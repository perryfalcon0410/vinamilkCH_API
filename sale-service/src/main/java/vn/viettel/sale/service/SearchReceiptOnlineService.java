package vn.viettel.saleservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.messaging.Response;

import java.util.Date;

public interface SearchReceiptOnlineService {
    Response<Page<ReceiptOnline>> searchReceiptOnline(String code, Long status, Date fromDate, Date toDate, Pageable pageable);
}
