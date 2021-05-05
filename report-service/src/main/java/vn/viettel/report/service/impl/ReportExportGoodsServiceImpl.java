package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ExportGoodsDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;
import java.util.List;

@Service
public class ReportExportGoodsServiceImpl implements ReportExportGoodsService {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>> index(Date fromExportDate, Date toExportDate, Date fromOrderDate, Date toOrderDate
            , String lstProduct, String lstExportType, String searchKeywords, Pageable pageable) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_EXPORT_GOODS", ExportGoodsDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);

        storedProcedure.setParameter(2, fromExportDate);
        storedProcedure.setParameter(3, toExportDate);
        storedProcedure.setParameter(4, fromOrderDate);
        storedProcedure.setParameter(5, toOrderDate);
        storedProcedure.setParameter(6, lstProduct);
        storedProcedure.setParameter(7, lstExportType);
        storedProcedure.setParameter(8, searchKeywords);
        storedProcedure.execute();

        List<ExportGoodsDTO> lst = storedProcedure.getResultList();
        Integer l = lst.size();
        ExportGoodsDTO lastExportGood = lst.get(l-1);
        TotalReport totalReport = new TotalReport();
        totalReport.setTotalAmount(lastExportGood.getTotalAmount());
        totalReport.setTotalQuantity(lastExportGood.getQuantity());
        totalReport.setTotalAmountNotVat(lastExportGood.getAmountNotVat());
        totalReport.setTotalPacketQuantity(lastExportGood.getPacketQuantity());
        totalReport.setTotalUnitQuantity(lastExportGood.getUnitQuantity());

        lst.remove(l-1);
        lst.remove(l-2);
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lst.size());
        Page<ExportGoodsDTO>  page = new PageImpl<>(lst.subList(start, end), pageable, lst.size());

        CoverResponse coverResponse = new CoverResponse(page,totalReport);
        return new Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>>().withData(coverResponse);
    }
}
