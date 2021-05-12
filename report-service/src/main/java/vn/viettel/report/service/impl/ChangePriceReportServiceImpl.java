package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class ChangePriceReportServiceImpl implements ChangePriceReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>> index(String searchKey, Date fromTransDate, Date toTransDate,
                                                                                    Date fromOrderDate, Date toOrderDate, String ids, Pageable pageable) throws ParseException {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_CHANGE_PRICE", ChangePriceDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);

        storedProcedure.setParameter(2, searchKey);
        storedProcedure.setParameter(3, fromTransDate);
        storedProcedure.setParameter(4, toTransDate);
        storedProcedure.setParameter(5, fromOrderDate);
        storedProcedure.setParameter(6, toOrderDate);
        storedProcedure.setParameter(7, ids);

        List<ChangePriceDTO> result = storedProcedure.getResultList();
        ChangePriceDTO changePriceTotal = result.get(result.size() - 1);
        List<ChangePriceDTO> response = result.subList(0, result.size() - 2);

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), response.size());
        List<ChangePriceDTO> subList = response.subList(start, end);

        return new Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>>().withData(new CoverResponse<>(new PageImpl<>(subList),
                new ChangePriceTotalDTO("", changePriceTotal.getQuantity(), changePriceTotal.getTotalInput(), changePriceTotal.getTotalOutput())));
    }
}
