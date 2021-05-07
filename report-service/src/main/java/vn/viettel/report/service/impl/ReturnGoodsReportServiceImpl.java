package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.dto.ReturnGoodsReportDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportTotalDTO;


import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReturnGoodsReportServiceImpl implements ReturnGoodsReportService {

    @PersistenceContext
    EntityManager entityManager;

    private List<ReturnGoodsReportDTO> callStoreProcedure(Long shopId, String reciept, Date fromDate, Date toDate, String reason, String productIds) {
        Instant inst = fromDate.toInstant();
        LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
        Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date startDate = Date.from(dayInst);

        LocalDateTime localDateTime = LocalDateTime
                .of(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_RETURNED_GOODS", ReturnGoodsReportDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);

        query.setParameter(2, Integer.valueOf(shopId.toString()));
        query.setParameter(3, reciept);
        query.setParameter(4, fromDate);
        query.setParameter(5, toDate);
        query.setParameter(6, reason);
        query.setParameter(7, productIds);

        query.execute();

        List<ReturnGoodsReportDTO> reportDTOS =  query.getResultList();
        return reportDTOS;
    }

    @Override
    public Response<CoverResponse<Page<ReturnGoodsReportDTO>, ReturnGoodsReportTotalDTO>> getReturnGoodsReport(ReturnGoodsReportsFilter filter, Pageable pageable) {
        List<ReturnGoodsReportDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getReciept(), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductIds());

        Integer totalQuantity = 0;
        Float totalAmount = 0F;
        Float totalRefunds = 0F;

        for (ReturnGoodsReportDTO dto : reportDTOS) {
            totalQuantity += dto.getQuantity();
            totalAmount += dto.getAmount();
            totalRefunds += dto.getRefunds();
        }

        ReturnGoodsReportTotalDTO dto = new ReturnGoodsReportTotalDTO(
                totalQuantity, totalAmount, totalRefunds);
        Page<ReturnGoodsReportDTO> reportDTOList = new PageImpl<>(reportDTOS);
        CoverResponse<Page<ReturnGoodsReportDTO>, ReturnGoodsReportTotalDTO> response = new CoverResponse<>(reportDTOList, dto);
        return new Response<CoverResponse<Page<ReturnGoodsReportDTO>, ReturnGoodsReportTotalDTO>>().withData(response);

    }

}
