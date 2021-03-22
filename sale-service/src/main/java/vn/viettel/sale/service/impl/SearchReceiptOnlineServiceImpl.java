package vn.viettel.sale.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.ReceiptOnlineRepository;
import vn.viettel.sale.service.SearchReceiptOnlineService;
import vn.viettel.sale.specification.ReceiptOnlineSpecification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class SearchReceiptOnlineServiceImpl extends BaseServiceImpl<ReceiptOnline, ReceiptOnlineRepository> implements SearchReceiptOnlineService {

    @Override
    public Response<Page<ReceiptOnline>> searchReceiptOnline(String code, Long status, Date fromDate, Date toDate, Pageable pageable) {
        Response<Page<ReceiptOnline>> response = new Response<>();

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        Page<ReceiptOnline> receiptOnlines;
        receiptOnlines = repository.findAll(Specification.where(ReceiptOnlineSpecification.hasCode(code))
                        .and(ReceiptOnlineSpecification.hasFromDateToDate(fromDate, toDate))
                        .and(ReceiptOnlineSpecification.hasStatus(status)), pageable);

        return response.withData(receiptOnlines);
    }
}
