package vn.viettel.report.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.report.service.dto.ReturnGoodsReportDTO;



import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.time.*;
import java.util.Date;
import java.util.List;

@Service
public class ReturnGoodsReportServiceImpl {

    @PersistenceContext
    EntityManager entityManager;

    private List<ReturnGoodsReportDTO> callStoreProcedure(String reciept, Date fromDate, Date toDate,String reason,String productIds){
        Instant inst = fromDate.toInstant();
        LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
        Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date startDate = Date.from(dayInst);

        LocalDateTime localDateTime = LocalDateTime
                .of(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("p_returned_goods", ReturnGoodsReportDTO.class);
        query.registerStoredProcedureParameter("resdata" , void.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("reciept" , String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("fromDate" , Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("toDate" , Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("reason" , String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("productIds" , String.class, ParameterMode.IN);

        query.setParameter("reciept" , reciept);
        query.setParameter("fromDate" , startDate);
        query.setParameter("toDate" , endDate);
        query.setParameter("reason" , reason);
        query.setParameter("productIds" , productIds);

        query.execute();

        List<ReturnGoodsReportDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }


}
