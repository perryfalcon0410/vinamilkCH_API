package vn.viettel.report.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.messaging.ExchangeTransTotal;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.dto.PromotionProductDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;
import vn.viettel.report.service.excel.ExchangeTransExcel;
import vn.viettel.report.service.feign.ShopClient;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeTransReportServiceImpl implements ExchangeTransReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        List<ExchangeTransReportDTO> exchangeTransList = this.callStoreProcedure();
        ExchangeTransReportDTO exchangeTransTotal = new ExchangeTransReportDTO();
        if(!exchangeTransList.isEmpty()) {
            exchangeTransTotal = exchangeTransList.get(exchangeTransList.size() -1);
            this.removeDataList(exchangeTransList);
        }
        ExchangeTransExcel excel = new ExchangeTransExcel(shopDTO, exchangeTransList, exchangeTransTotal);
        excel.setFromDate(filter.getFromDate());
        excel.setToDate(filter.getToDate());
        return excel.export();
    }

    private List<ExchangeTransReportDTO> callStoreProcedure() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_EXCHANGE_TRANS", ExchangeTransReportDTO.class);
        query.registerStoredProcedureParameter("EXCHANGE_TRANS", void.class,  ParameterMode.REF_CURSOR);
        query.execute();
        List<ExchangeTransReportDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    public Response<CoverResponse<Page<ExchangeTransReportDTO>, ExchangeTransTotal>> getExchangeTransReport(ExchangeTransFilter filter, Pageable pageable) {
        List<ExchangeTransReportDTO> exchangeTransList = this.callStoreProcedure();
        ExchangeTransTotal totalDTO = new ExchangeTransTotal();
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
        return new Response<CoverResponse<Page<ExchangeTransReportDTO>, ExchangeTransTotal>>().withData(response);
    }

    private void removeDataList(List<ExchangeTransReportDTO> exchangeTrans) {
        exchangeTrans.remove(exchangeTrans.size()-1);
        exchangeTrans.remove(exchangeTrans.size()-1);
    }
}
