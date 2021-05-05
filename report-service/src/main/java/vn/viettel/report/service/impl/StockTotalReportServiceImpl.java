package vn.viettel.report.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

@Service
public class StockTotalReportServiceImpl implements StockTotalReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<StockTotalReportDTO> getStockTotalReport(String stockDate) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_STOCK_COUNTING", StockTotalReportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);

        storedProcedure.setParameter(2, stockDate);

        return storedProcedure.getResultList();
    }
}
