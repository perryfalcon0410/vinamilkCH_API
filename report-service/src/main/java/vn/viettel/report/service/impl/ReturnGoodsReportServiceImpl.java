package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReturnGoodsReportServiceImpl implements ReturnGoodsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<ReturnGoodsDTO> callStoreProcedure(Long shopId, String reciept, Date fromDate, Date toDate, String reason, String productIds) {
//        Instant inst = fromDate.toInstant();
//        LocalDate localDate = inst.atZone(ZoneId.systemDefault()).toLocalDate();
//        Instant dayInst = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
//        Date startDate = Date.from(dayInst);
//
//        LocalDateTime localDateTime = LocalDateTime
//                .of(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
//        Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_RETURNED_GOODS", ReturnGoodsDTO.class);
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

        List<ReturnGoodsDTO> reportDTOS =  query.getResultList();
        return reportDTOS;
    }

    @Override
    public Response<CoverResponse<Page<ReturnGoodsDTO>, ReturnGoodsReportTotalDTO>> getReturnGoodsReport(ReturnGoodsReportsFilter filter, Pageable pageable) {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getReciept(), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductIds());
        ReturnGoodsReportTotalDTO totalDTO = new ReturnGoodsReportTotalDTO();
        List<ReturnGoodsDTO> dtoList = new ArrayList<>();

       if (!reportDTOS.isEmpty()){
           ReturnGoodsDTO dto = reportDTOS.get(reportDTOS.size() - 1);
           totalDTO.setTotalQuantity(dto.getTotalQuantity());
           totalDTO.setTotalAmount(dto.getTotalAmount());
           totalDTO.setTotalRefunds(dto.getTotalRefunds());

           this.removeDataList(reportDTOS);
           int start = (int)pageable.getOffset();
           int end = Math.min((start + pageable.getPageSize()), reportDTOS.size());
           dtoList = reportDTOS.subList(start, end);
       }

        Page<ReturnGoodsDTO> page = new PageImpl<>( dtoList, pageable, reportDTOS.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return new Response<CoverResponse<Page<ReturnGoodsDTO>, ReturnGoodsReportTotalDTO>>().withData(response);

    }

    @Override
    public ByteArrayInputStream exportExcel(ReturnGoodsReportsFilter filter) throws IOException {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getReciept(), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductIds());
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ReturnGoodsDTO goodsReportDTO = new ReturnGoodsDTO();
        if (!reportDTOS.isEmpty()){
            goodsReportDTO = reportDTOS.get(reportDTOS.size() - 1);
            this.removeDataList(reportDTOS);
        }

        ReturnGoodsExcel excel = new ReturnGoodsExcel(shopDTO, reportDTOS, goodsReportDTO);
        excel.setFromDate(filter.getFromDate());
        excel.setToDate(filter.getToDate());
        return excel.export();
    }

    @Override
    public Response<PromotionProductReportDTO> getDataPrint(ReturnGoodsReportsFilter filter) {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getReciept(), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductIds());
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ReturnGoodsReportDTO goodsReportDTO = new ReturnGoodsReportDTO(filter.getFromDate() , filter.getToDate() , shopDTO);
        if (!reportDTOS.isEmpty()) {
            ReturnGoodsDTO dto = reportDTOS.get(reportDTOS.size() - 1);
            goodsReportDTO.setTotalQuantity(dto.getTotalQuantity());
            goodsReportDTO.setTotalAmount(dto.getTotalAmount());
            goodsReportDTO.setTotalRefunds(dto.getTotalRefunds());
            this.removeDataList(reportDTOS);


        }
        return null;
    }


    private void removeDataList(List<ReturnGoodsDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size()-1);
        reportDTOS.remove(reportDTOS.size()-1);
    }
}
