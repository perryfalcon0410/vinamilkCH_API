package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.CustomerReportDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerNotTradeServiceImpl implements CustomerNotTradeService {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Object index(Date fromDate, Date toDate, Boolean isPaging, Pageable pageable) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_CUSTOMER_NOT_TRADE", CustomerReportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);

        storedProcedure.setParameter(2, fromDate);
        storedProcedure.setParameter(3, toDate);

        List<CustomerReportDTO> result = storedProcedure.getResultList();

        if (result.isEmpty())
            return new Response<Page<CustomerReportDTO>>()
                    .withData(new PageImpl<>(new ArrayList<>()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        List<CustomerReportDTO> subList = result.subList(start, end);

        if (isPaging)
            return new Response<Page<CustomerReportDTO>>().withData(new PageImpl<>(subList));
        else
            return new Response<List<CustomerReportDTO>>().withData(result);
    }
}
