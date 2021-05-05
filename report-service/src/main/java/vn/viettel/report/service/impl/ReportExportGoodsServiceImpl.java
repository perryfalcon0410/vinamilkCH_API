package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ExportGoodsDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.Date;

@Service
public class ReportExportGoodsServiceImpl implements ReportExportGoodsService {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Response<Page<ExportGoodsDTO>> index(Date fromExportDate, Date toExportDate, Date fromOrderDate, Date toOrderDate
            , String lstProduct, String lstExportType, String searchKeywords, Pageable pageable) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_EXPORT_GOODS", ExportGoodsDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, void.class, ParameterMode.REF_CURSOR);

        storedProcedure.setParameter(1, fromExportDate);
        storedProcedure.setParameter(2, toExportDate);
        storedProcedure.setParameter(3, fromOrderDate);
        storedProcedure.setParameter(4, toOrderDate);
        storedProcedure.setParameter(5, lstProduct);
        storedProcedure.setParameter(6, lstExportType);
        storedProcedure.setParameter(7, searchKeywords);
        storedProcedure.execute();

        Page<ExportGoodsDTO> exportGoodsDTOS = new PageImpl<>(storedProcedure.getResultList(), pageable, storedProcedure.getResultList().size());
        return new Response<Page<ExportGoodsDTO>>().withData(exportGoodsDTOS);
    }
}
