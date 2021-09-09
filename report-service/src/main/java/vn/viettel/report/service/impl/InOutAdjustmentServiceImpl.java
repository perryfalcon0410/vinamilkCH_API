package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseReportServiceImpl;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.InOutAdjustmentService;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;
import vn.viettel.report.service.dto.InOutAdjustmentTotalDTO;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InOutAdjustmentServiceImpl extends BaseReportServiceImpl implements InOutAdjustmentService {

    @Override
    public CoverResponse<Page<InOutAdjusmentDTO>, InOutAdjustmentTotalDTO> find(InOutAdjustmentFilter filter, Pageable pageable) {
        List<InOutAdjusmentDTO> data =  this.callProcedure(filter).getData();
        InOutAdjustmentTotalDTO totalDTO = new InOutAdjustmentTotalDTO();
        List<InOutAdjusmentDTO> subList = new ArrayList<>();
        if(!data.isEmpty()) {
            InOutAdjusmentDTO total = data.get(data.size() -1);
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
        storedProcedure.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, Long.class, ParameterMode.IN);
        ///////////////////////////////////////////////////////////////////////////////////////////
        storedProcedure.setParameter(2, filter.getFromDate());
        storedProcedure.setParameter(3, filter.getToDate());
        storedProcedure.setParameter(4, filter.getProductCodes());
        storedProcedure.setParameter(5, filter.getShopId());
        this.executeQuery(storedProcedure, "P_IN_OUT_ADJUSTMENT", filter.toString());
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
        shopImports.remove(shopImports.get(shopImports.size() -1));
        shopImports.remove(shopImports.get(shopImports.size() -1));
    }
}
