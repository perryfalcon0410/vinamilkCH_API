package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.InOutAdjustmentService;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;
import vn.viettel.report.service.dto.InOutAdjustmentTotalDTO;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InOutAdjustmentServiceImpl implements InOutAdjustmentService {
    @PersistenceContext
    EntityManager entityManager;
    @Override
    public CoverResponse<Page<InOutAdjusmentDTO>, InOutAdjustmentTotalDTO> find(InOutAdjustmentFilter filter, Pageable pageable) {
        List<InOutAdjusmentDTO> data =  this.callProcedure(filter).getData();
        InOutAdjustmentTotalDTO totalDTO = new InOutAdjustmentTotalDTO();
        List<InOutAdjusmentDTO> subList = new ArrayList<>();
        if(!data.isEmpty()) {
            InOutAdjusmentDTO total = data.get(0);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalPrice(total.getTotal());
            this.removeDataList(data);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), data.size());
            subList = data.subList(start, end);
        }
        Page<InOutAdjusmentDTO> page = new PageImpl<>( subList, pageable, data.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }

    @Override
    public Response<List<InOutAdjusmentDTO>> callProcedure(InOutAdjustmentFilter filter) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_IN_OUT_ADJUSTMENT", InOutAdjusmentDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        ///////////////////////////////////////////////////////////////////////////////////////////
        storedProcedure.setParameter(2, filter.getFromDate());
        storedProcedure.setParameter(3, filter.getToDate());
        storedProcedure.setParameter(4, filter.getProductCodes());
        storedProcedure.execute();
        List<InOutAdjusmentDTO> data =  storedProcedure.getResultList();
        return new Response<List<InOutAdjusmentDTO>>().withData(data);
    }

    @Override
    public List<InOutAdjusmentDTO> dataExcel(InOutAdjustmentFilter filter) {
        List<InOutAdjusmentDTO> data =  this.callProcedure(filter).getData();
        if(!data.isEmpty()) {
            this.removeDataList(data);
        }
        return data;
    }
    private void removeDataList(List<InOutAdjusmentDTO> shopImports) {
        shopImports.remove(shopImports.get(0));
        shopImports.remove(shopImports.get(0));
    }
}
