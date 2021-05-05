package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.ShopImportDTO;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

@Service
public class ShopImportReportServiceImpl implements ShopImportReportService {
    @PersistenceContext
    EntityManager entityManager;
    @Override
    public Response<Page<ShopImportDTO>> find(ShopImportFilter filter, Pageable pageable) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_SHOP_IMPORT", ShopImportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);
        ///////////////////////////////////////////////////////////////////////////////////////////
        String lstProductId = null;
        if(filter.getProductIds()!= null){
            lstProductId = filter.getProductIds().get(0).toString();
            for (int i =1 ;i<filter.getProductIds().size();i++){
                lstProductId = lstProductId + ","+ filter.getProductIds().get(i).toString();
            }
        }
        storedProcedure.setParameter(2, filter.getFromDate());
        storedProcedure.setParameter(3, filter.getToDate());
        storedProcedure.setParameter(4, lstProductId);
        storedProcedure.setParameter(5, filter.getImportType());
        storedProcedure.setParameter(6, filter.getInternalNumber());
        storedProcedure.setParameter(7, filter.getFromOrderDate());
        storedProcedure.setParameter(8, filter.getToOrderDate());
        storedProcedure.execute();
        Page<ShopImportDTO> response = new PageImpl<>(storedProcedure.getResultList());
        return new Response<Page<ShopImportDTO>>().withData(response);
    }
}
