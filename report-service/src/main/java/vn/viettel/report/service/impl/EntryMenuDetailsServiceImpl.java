package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.EntryMenuDetailsReportsFilter;
import vn.viettel.report.service.EntryMenuDetailsReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.excel.EntryMenuDetailsExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EntryMenuDetailsServiceImpl implements EntryMenuDetailsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<EntryMenuDetailsDTO> callStoreProcedure(Long shopId,  Date fromDate, Date toDate) {
//        Instant inst = fromDate.toInstant();
//        LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
//        Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
//        Date startDate = Date.from(dayInst);
//
//        LocalDateTime localDateTime = LocalDateTime
//                .of(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
//        Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_ENTRY_MENU_DETAILS", EntryMenuDetailsDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);

        query.setParameter(2, Integer.valueOf(shopId.toString()));
        query.setParameter(3, fromDate);
        query.setParameter(4, toDate);

        query.execute();

        List<EntryMenuDetailsDTO> reportDTOS =  query.getResultList();
        return reportDTOS;
    }

    @Override
    public Response<CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO>> getEntryMenuDetailsReport(EntryMenuDetailsReportsFilter filter, Pageable pageable) {
        List<EntryMenuDetailsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getFromDate(), filter.getToDate());
        ReportTotalDTO totalDTO = new ReportTotalDTO();
        List<EntryMenuDetailsDTO> dtoList = new ArrayList<>();

       if (!reportDTOS.isEmpty()){
           EntryMenuDetailsDTO dto = reportDTOS.get(reportDTOS.size() - 1);
           totalDTO.setTotalAmount(dto.getTotalAmount());

           this.removeDataList(reportDTOS);
           int start = (int)pageable.getOffset();
           int end = Math.min((start + pageable.getPageSize()), reportDTOS.size());
           dtoList = reportDTOS.subList(start, end);
       }

        Page<EntryMenuDetailsDTO> page = new PageImpl<>( dtoList, pageable, reportDTOS.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return new Response<CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO>>().withData(response);

    }

    @Override
    public ByteArrayInputStream exportExcel(EntryMenuDetailsReportsFilter filter) throws IOException {
        List<EntryMenuDetailsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getFromDate(), filter.getToDate());
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        EntryMenuDetailsDTO entryMenuDetailsDTO = new EntryMenuDetailsDTO();
        if (!reportDTOS.isEmpty()){
            entryMenuDetailsDTO = reportDTOS.get(reportDTOS.size() - 1);
            this.removeDataList(reportDTOS);
        }

        EntryMenuDetailsExcel excel = new EntryMenuDetailsExcel(shopDTO, reportDTOS, entryMenuDetailsDTO,filter);
        return excel.export();
    }

    @Override
    public Response<CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO>> getEntryMenuDetails(EntryMenuDetailsReportsFilter filter) {
        List<EntryMenuDetailsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getFromDate(), filter.getToDate());
        ReportDateDTO dateDTO = new ReportDateDTO();

        if (!reportDTOS.isEmpty()){
            dateDTO.setFromDate(filter.getFromDate());
            dateDTO.setToDate(filter.getToDate());
            String dateOfPrinting = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            dateDTO.setDateOfPrinting(dateOfPrinting);
        }
        CoverResponse response = new CoverResponse(reportDTOS, dateDTO);
        return new Response<CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO>>().withData(response);
    }


    private void removeDataList(List<EntryMenuDetailsDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size()-1);
        reportDTOS.remove(reportDTOS.size()-1);
    }
}
