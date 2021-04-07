package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.sale.RedInvoice;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.SearchRequest;
import vn.viettel.sale.repository.RedInvoiceRepository;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.specification.RedInvoiceSpefication;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class RedInvoiceServiceImpl extends BaseServiceImpl<RedInvoice, RedInvoiceRepository> implements RedInvoiceService {
    @Autowired
    CustomerClient customerClient;

    @Override
    public Response<Page<RedInvoice>> getAll(String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable) {
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        List<Long> ids = customerClient.getIdCustomerBySearchKeyWords(searchKeywords).getData();
        Page<RedInvoice> redInvoices = null;
        for(Long id : ids)
        {
            redInvoices = repository.findAll(Specification.where(RedInvoiceSpefication.hasCustomerId(id)
                    .and(RedInvoiceSpefication.hasInvoiceNumber(invoiceNumber)
                    .and(RedInvoiceSpefication.hasFromDateToDate(fromDate,toDate)))),pageable);
        }
        return new Response<Page<RedInvoice>>().withData(redInvoices);
    }
}
