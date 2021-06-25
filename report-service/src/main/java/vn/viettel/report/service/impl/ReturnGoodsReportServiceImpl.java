package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.ChangeReturnGoodsReportRequest;
import vn.viettel.report.messaging.ReturnGoodsReportsRequest;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.excel.ReturnGoodsExcel;
import vn.viettel.report.service.feign.ShopClient;


import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class ReturnGoodsReportServiceImpl implements ReturnGoodsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<ReturnGoodsDTO> callStoreProcedure(Long shopId, String reciept, LocalDate fromDate, LocalDate toDate, String reason, String productKW) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_RETURNED_GOODS", ReturnGoodsDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, LocalDate.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, LocalDate.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);

        query.setParameter(2, Integer.valueOf(shopId.toString()));
        query.setParameter(3, reciept);
        query.setParameter(4, fromDate);
        query.setParameter(5, toDate);
        query.setParameter(6, reason);
        query.setParameter(7, productKW);

        query.execute();

        List<ReturnGoodsDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    @Override
    public CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO> getReturnGoodsReport(ReturnGoodsReportsRequest filter, Pageable pageable) {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getReciept().toUpperCase(Locale.ROOT), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductKW().toUpperCase(Locale.ROOT));
        ReportTotalDTO totalDTO = new ReportTotalDTO();
        List<ReturnGoodsDTO> dtoList = new ArrayList<>();

        if (!reportDTOS.isEmpty()) {
            ReturnGoodsDTO dto = reportDTOS.get(reportDTOS.size() - 1);
            totalDTO.setTotalQuantity(dto.getTotalQuantity());
            totalDTO.setTotalAmount(dto.getTotalAmount());
            totalDTO.setTotalRefunds(dto.getTotalRefunds());

            this.removeDataList(reportDTOS);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), reportDTOS.size());
            dtoList = reportDTOS.subList(start, end);
        }

        Page<ReturnGoodsDTO> page = new PageImpl<>(dtoList, pageable, reportDTOS.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }

    @Override
    public ByteArrayInputStream exportExcel(ReturnGoodsReportsRequest filter) throws IOException {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getReciept().toUpperCase(Locale.ROOT), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductKW().toUpperCase(Locale.ROOT));
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ReturnGoodsDTO goodsReportDTO = new ReturnGoodsDTO();
        ReturnGoodsReportTotalDTO totalDTO = new ReturnGoodsReportTotalDTO();
        if (!reportDTOS.isEmpty()) {
            goodsReportDTO = reportDTOS.get(reportDTOS.size() - 1);
            totalDTO.setTotalQuantity(goodsReportDTO.getTotalQuantity());
            totalDTO.setTotalAmount(goodsReportDTO.getTotalAmount());
            totalDTO.setTotalRefunds(goodsReportDTO.getTotalRefunds());
            this.removeDataList(reportDTOS);
        }
        ChangeReturnGoodsReportRequest reportRequest = new ChangeReturnGoodsReportRequest(totalDTO, reportDTOS);
        ReturnGoodsExcel excel = new ReturnGoodsExcel(shopDTO, reportRequest, filter);
        return excel.export();
    }

    private List<ReturnGoodsDTO> callStoreProcedurePrint(Long shopId, String reciept, LocalDate fromDate, LocalDate toDate, String reason, String productKW) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_PRINT_RETURN_GOODS", ReturnGoodsDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, LocalDate.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, LocalDate.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);

        query.setParameter(2, Integer.valueOf(shopId.toString()));
        query.setParameter(3, reciept);
        query.setParameter(4, fromDate);
        query.setParameter(5, toDate);
        query.setParameter(6, reason);
        query.setParameter(7, productKW);

        query.execute();

        List<ReturnGoodsDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    @Override
    public CoverResponse<List<ReportPrintIndustryTotalDTO>, ReportPrintTotalDTO> getDataPrint(ReturnGoodsReportsRequest filter) {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedurePrint(
                filter.getShopId(), filter.getReciept().toUpperCase(Locale.ROOT), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductKW().toUpperCase(Locale.ROOT)  );
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ReportPrintTotalDTO totalDTO = new ReportPrintTotalDTO();
        List<ReportPrintIndustryTotalDTO> printIndustryTotalDTOS = new ArrayList<>();
        for (int i = 0; i < reportDTOS.size(); i++) {
            for (int j = i + 1; j < reportDTOS.size(); j++) {
                if (reportDTOS.get(i).getIndustry().equals(reportDTOS.get(j).getIndustry())){

                }
            }
        }
        if (!reportDTOS.isEmpty()) {
            ReturnGoodsDTO dto = reportDTOS.get(reportDTOS.size() - 1);
            totalDTO.setTotalQuantity(dto.getTotalQuantity());
            totalDTO.setTotalAmount(dto.getTotalAmount());
            totalDTO.setTotalRefunds(dto.getTotalRefunds());
            totalDTO.setShopDTO(shopDTO);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fromDate = formatter.format(filter.getFromDate());
            totalDTO.setFromDate(fromDate);
            String toDate = formatter.format(filter.getToDate());
            totalDTO.setToDate(toDate);
            totalDTO.setPrintDate(LocalDateTime.now());
        }

        CoverResponse response = new CoverResponse(reportDTOS, totalDTO);
        return response;
    }


    private void removeDataList(List<ReturnGoodsDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size() - 1);
        reportDTOS.remove(reportDTOS.size() - 1);
    }
}
