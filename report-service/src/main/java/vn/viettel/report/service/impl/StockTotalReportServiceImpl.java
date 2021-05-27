package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;
import java.util.List;

@Service
public class StockTotalReportServiceImpl implements StockTotalReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO> getStockTotalReport(Date stockDate, String productCodes, Long shopId, Pageable pageable) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_STOCK_COUNTING", StockTotalReportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, Long.class, ParameterMode.IN);

        storedProcedure.setParameter(2, stockDate);
        storedProcedure.setParameter(3, productCodes);
        storedProcedure.setParameter(4, shopId);

        List<StockTotalReportDTO> listResult = storedProcedure.getResultList();

        if (listResult.isEmpty())
            return new CoverResponse<>(new PageImpl<>(listResult), null);

        StockTotalReportDTO totalInfo = listResult.get(listResult.size() - 1);
        List<StockTotalReportDTO> response = listResult.subList(0, listResult.size() - 2);

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), response.size());
        List<StockTotalReportDTO> subList = response.subList(start, end);

        return new CoverResponse<>(new PageImpl<>(subList),
                new StockTotalInfoDTO(totalInfo.getStockQuantity(), totalInfo.getPacketQuantity(), totalInfo.getUnitQuantity(), totalInfo.getTotalAmount()));
    }
}
