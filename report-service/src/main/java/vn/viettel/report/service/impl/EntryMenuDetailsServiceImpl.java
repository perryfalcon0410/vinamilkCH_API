package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.EntryMenuDetailsReportsRequest;
import vn.viettel.report.service.EntryMenuDetailsReportService;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;
import vn.viettel.report.service.excel.EntryMenuDetailsExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntryMenuDetailsServiceImpl implements EntryMenuDetailsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<EntryMenuDetailsDTO> callStoreProcedure(Long shopId, LocalDateTime fromDate, LocalDateTime toDate) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_ENTRY_MENU_DETAILS", EntryMenuDetailsDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, LocalDateTime.class, ParameterMode.IN);

        query.setParameter(2, Integer.valueOf(shopId.toString()));
        query.setParameter(3, fromDate);
        query.setParameter(4, toDate);

        query.execute();

        List<EntryMenuDetailsDTO> reportDTOS =  query.getResultList();
        entityManager.close();
        return reportDTOS;
    }

    @Override
    public CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO> getEntryMenuDetailsReport(EntryMenuDetailsReportsRequest filter, Pageable pageable) {
        List<EntryMenuDetailsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getFromDate(), filter.getToDate());
        ReportTotalDTO totalDTO = new ReportTotalDTO();
        List<EntryMenuDetailsDTO> dtoList = new ArrayList<>();

       if (!reportDTOS.isEmpty()){
           EntryMenuDetailsDTO dto = reportDTOS.get(reportDTOS.size() -1);
           totalDTO.setTotalAmount(dto.getTotalAmount());

           this.removeDataList(reportDTOS);
           int start = (int)pageable.getOffset();
           int end = Math.min((start + pageable.getPageSize()), reportDTOS.size());
           dtoList = reportDTOS.subList(start, end);
       }

        Page<EntryMenuDetailsDTO> page = new PageImpl<>( dtoList, pageable, reportDTOS.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;

    }

    @Override
    public ByteArrayInputStream exportExcel(EntryMenuDetailsReportsRequest filter) throws IOException {
        List<EntryMenuDetailsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getFromDate(), filter.getToDate());
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        EntryMenuDetailsDTO entryMenuDetailsDTO = new EntryMenuDetailsDTO();
        if (!reportDTOS.isEmpty()){
            entryMenuDetailsDTO = reportDTOS.get(reportDTOS.size() -1);
            this.removeDataList(reportDTOS);
        }

        EntryMenuDetailsExcel excel = new EntryMenuDetailsExcel(shopDTO, shopDTO.getParentShop(), reportDTOS, entryMenuDetailsDTO,filter);
        return excel.export();
    }

    @Override
    public CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO> getEntryMenuDetails(EntryMenuDetailsReportsRequest filter) {
        List<EntryMenuDetailsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getFromDate(), filter.getToDate());
        ReportDateDTO dateDTO = new ReportDateDTO();

        if (!reportDTOS.isEmpty()){
            EntryMenuDetailsDTO entryMenuDetailsDTO = reportDTOS.get(reportDTOS.size() -1);
            dateDTO.setFromDate(filter.getFromDate());
            dateDTO.setToDate(filter.getToDate());
            dateDTO.setDateOfPrinting(DateUtils.formatDate2StringDate(LocalDate.now()));
            ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
            if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
            dateDTO.setShopName(shopDTO.getShopName());
            dateDTO.setAddress(shopDTO.getAddress());
            dateDTO.setTotalAmount(entryMenuDetailsDTO.getTotalAmount());
            this.removeDataList(reportDTOS);
        }
        CoverResponse response = new CoverResponse(reportDTOS, dateDTO);
        return response;
    }


    private void removeDataList(List<EntryMenuDetailsDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size() -1);
        reportDTOS.remove(reportDTOS.size() -1);
    }
}
