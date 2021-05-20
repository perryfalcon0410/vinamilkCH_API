package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.service.ReportVoucherService;
import vn.viettel.report.service.dto.ReportVoucherDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportVoucherServiceImpl implements ReportVoucherService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public Response<Page<ReportVoucherDTO>> index(ReportVoucherFilter filter, Pageable pageable) {
        StoredProcedureQuery storedProcedure = callReportVoucherDTO(filter);
        List<ReportVoucherDTO> lst = storedProcedure.getResultList();
        List<ReportVoucherDTO> subList = new ArrayList<>();

        if(!lst.isEmpty())
        {
            Integer l = lst.size();
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), lst.size());
            subList = lst.subList(start, end);
        }

        Page<ReportVoucherDTO>  page = new PageImpl<>(subList, pageable, lst.size());
        return new Response<Page<ReportVoucherDTO>>().withData(page);
    }

    @Override
    public ByteArrayInputStream exportExcel(ReportVoucherFilter filter) throws IOException {
        return null;
    }

    public StoredProcedureQuery callReportVoucherDTO(ReportVoucherFilter filter)
    {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_VOUCHER", ReportVoucherDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(9, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(10, Long.class, ParameterMode.IN);

        storedProcedure.setParameter(2, filter.getFromProgramDate());
        storedProcedure.setParameter(3, filter.getToProgramDate());
        storedProcedure.setParameter(4, filter.getFromUseDate());
        storedProcedure.setParameter(5, filter.getToUseDate());
        storedProcedure.setParameter(6, filter.getVoucherProgramName());
        storedProcedure.setParameter(7, filter.getVoucherKeywords());
        storedProcedure.setParameter(8, filter.getCustomerKeywords());
        storedProcedure.setParameter(9, filter.getCustomerMobiPhone());
        storedProcedure.setParameter(10, filter.getShopId());
        storedProcedure.execute();
        return storedProcedure;
    }
}