package vn.viettel.sale.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.sale.RedInvoice;
import vn.viettel.core.db.entity.sale.RedInvoiceDetail;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.SearchRequest;
import vn.viettel.sale.repository.RedInvoiceRepository;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.RedInvoiceService;
import vn.viettel.sale.service.dto.RedInvoiceDTO;
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

    @Autowired
    RedInvoiceDetailService redInvoiceDetailService;

    @Override
    public Response<Page<RedInvoiceDTO>> getAll(RedInvoiceFilter redInvoiceFilter, Pageable pageable) {
        String searchKeywords = redInvoiceFilter.getSearchKeywords();
        searchKeywords = StringUtils.defaultIfBlank(redInvoiceFilter.getSearchKeywords(), StringUtils.EMPTY);
        Date fromDate = redInvoiceFilter.getFromDate();
        Date toDate = redInvoiceFilter.getToDate();

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        List<Long> ids = customerClient.getIdCustomerBySearchKeyWords(searchKeywords).getData();
        Page<RedInvoice> redInvoices = null;
        if(searchKeywords.equals(""))
        {
            redInvoices = repository.findAll(Specification.where(RedInvoiceSpefication.hasFromDateToDate(fromDate,toDate))
                    .and(RedInvoiceSpefication.hasInvoiceNumber(redInvoiceFilter.getInvoiceNumber())),pageable);
        }else{
            for(Long id : ids)
            {
                redInvoices = repository.findAll(Specification.where(RedInvoiceSpefication.hasCustomerId(id))
                        .and(RedInvoiceSpefication.hasFromDateToDate(fromDate,toDate))
                        .and(RedInvoiceSpefication.hasInvoiceNumber(redInvoiceFilter.getInvoiceNumber())),pageable);
            }
        }

        Page<RedInvoiceDTO> redInvoiceDTOS = redInvoices.map(red->modelMapper.map(red,RedInvoiceDTO.class));

        redInvoiceDTOS.forEach(redInvoiceDTO -> {
            RedInvoiceDetail redInvoiceDetail = redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(redInvoiceDTO.getId()).getData();
            redInvoiceDTO.setAmountNotVat(redInvoiceDetail.getAmountNotVat());
            redInvoiceDTO.setAmountGTGT(redInvoiceDetail.getAmount()-redInvoiceDetail.getAmountNotVat());
        });

        return new Response<Page<RedInvoiceDTO>>().withData(redInvoiceDTOS);
    }
}
