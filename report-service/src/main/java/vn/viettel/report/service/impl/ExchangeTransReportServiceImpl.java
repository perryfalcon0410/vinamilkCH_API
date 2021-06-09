package vn.viettel.report.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.ExchangeTransTotalDTO;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.dto.ExchangeTransReportFullDTO;
import vn.viettel.report.service.dto.ExchangeTransReportRateDTO;
import vn.viettel.report.service.excel.ExchangeTransExcel;
import vn.viettel.report.service.feign.CommonClient;
import vn.viettel.report.service.feign.ShopClient;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExchangeTransReportServiceImpl implements ExchangeTransReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CommonClient commonClient;

    @Override
    public ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ExchangeTransReportFullDTO exchangeTransFull = this.callStoreProcedure(filter);
        ExchangeTransReportDTO exchangeTransTotal = new ExchangeTransReportDTO();
        List<ExchangeTransReportDTO> exchangeTransList = exchangeTransFull.getListData();
        List<ExchangeTransReportRateDTO> exchangeRate = exchangeTransFull.getSales();
        if(!exchangeTransList.isEmpty()) {
            exchangeTransTotal = exchangeTransList.get(exchangeTransList.size() -1);
            this.removeDataList(exchangeTransList);
        }
        ExchangeTransExcel excel = new ExchangeTransExcel(shopDTO, exchangeTransList, exchangeTransTotal, exchangeRate);
        excel.setFromDate(filter.getFromDate());
        excel.setToDate(filter.getToDate());
        return excel.export();
    }

    private ExchangeTransReportFullDTO callStoreProcedure(ExchangeTransFilter filter) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_EXCHANGE_TRANS", ExchangeTransReportDTO.class);
        query.registerStoredProcedureParameter("EXCHANGE_TRANS", void.class,  ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("SALES", Integer.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter("transCode", String.class,  ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("reason", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productKW", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("shopId", Integer.class, ParameterMode.IN);


        query.setParameter("transCode",filter.getTransCode());
        query.setParameter("fromDate", DateUtils.convertFromDate(filter.getFromDate()));
        query.setParameter("toDate", DateUtils.convertToDate(filter.getToDate()));
        query.setParameter("reason", filter.getReason());
        query.setParameter("productKW", filter.getProductKW());
        query.setParameter("shopId", Integer.valueOf(filter.getShopId().toString()));
        query.execute();
        List<ExchangeTransReportDTO> reportDTOS = query.getResultList();
        List<ExchangeTransReportRateDTO> reportDTOS1 = new ArrayList<>();
        if(query.hasMoreResults())
            reportDTOS1 = query.getResultList();
        ExchangeTransReportFullDTO reportFullDTOS = new ExchangeTransReportFullDTO();
        reportFullDTOS.setListData(reportDTOS);
        reportFullDTOS.setSales(reportDTOS1);
        return reportFullDTOS;
    }

    @Override
    public CoverResponse<Page<ExchangeTransReportDTO>, ExchangeTransTotalDTO> getExchangeTransReport(ExchangeTransFilter filter, Pageable pageable) {
        ExchangeTransReportFullDTO exchangeTransFull = this.callStoreProcedure(filter);
        List<ExchangeTransReportDTO> exchangeTransList = exchangeTransFull.getListData();
        ExchangeTransTotalDTO totalDTO = new ExchangeTransTotalDTO();
        List<ExchangeTransReportDTO> subList = new ArrayList<>();
        if(!exchangeTransList.isEmpty()) {
            ExchangeTransReportDTO total = exchangeTransList.get(exchangeTransList.size()-1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalAmount(total.getAmount());

            this.removeDataList(exchangeTransList);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), exchangeTransList.size());
            subList = exchangeTransList.subList(start, end);
        }
        Page<ExchangeTransReportDTO> page = new PageImpl<>( subList, pageable, exchangeTransList.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }

    public List<CategoryDataDTO> listReasonExchange() {
        List<CategoryDataDTO> reasons = commonClient.getReasonExchangeV1().getData();
        return reasons;
    }

    private void removeDataList(List<ExchangeTransReportDTO> exchangeTrans) {
        exchangeTrans.remove(exchangeTrans.size()-1);
        exchangeTrans.remove(exchangeTrans.size()-1);
    }
}
