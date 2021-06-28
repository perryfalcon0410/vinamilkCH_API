package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReturnGoodsReportServiceImpl implements ReturnGoodsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<ReturnGoodsDTO> callStoreProcedure(Long shopId, String reciept, Date fromDate, Date toDate, String reason, String productKW) {

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


    @Override
    public ReportPrintIndustryTotalDTO getDataPrint(ReturnGoodsReportsRequest filter) {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        ReportPrintIndustryTotalDTO printDTO = new ReportPrintIndustryTotalDTO();
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getReciept().toUpperCase(Locale.ROOT), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductKW().toUpperCase(Locale.ROOT));
        ReturnGoodsDTO totalInfo = reportDTOS.get(reportDTOS.size() - 1);
        List<ReturnGoodsDTO> listResults = reportDTOS.subList(0, reportDTOS.size() - 2);
        Map<Long, List<ReturnGoodsDTO>> cats = listResults.stream().collect(Collectors.groupingBy(ReturnGoodsDTO::getIndustryId));
        List<ReportPrintOrderTotalDTO> dataByCat = new ArrayList<>();
        printDTO.setTotalInfo(totalInfo);
        for (Map.Entry<Long, List<ReturnGoodsDTO>> entry : cats.entrySet()) {
            ReportPrintOrderTotalDTO cat = new ReportPrintOrderTotalDTO();
            Integer totalQuantity = 0;
            Float totalAmount = 0F;
            Float totalRefunds = 0F;
            for(ReturnGoodsDTO total : entry.getValue()) {
                cat.setCategory(total.getIndustry());
//                cat.setReturnCode(total.getReturnCode());
//                cat.setOrderCode(total.getReciept());
//                cat.setCustomerName(total.getFullName());
                totalQuantity += total.getQuantity();
                totalAmount += total.getAmount();
                totalRefunds +=total.getRefunds();
            }
            cat.setTotalQuantity(totalQuantity);
            cat.setTotalAmount(totalAmount);
            cat.setTotalRefunds(totalRefunds);
            cat.setReportPrintProductDTOS(cats.get(entry.getKey()));
            dataByCat.add(cat);
        }
        printDTO.setShopName(shopDTO.getShopName());
        printDTO.setShopTel(shopDTO.getMobiPhone());
        printDTO.setAddress(shopDTO.getAddress());
        printDTO.setFromDate(DateUtils.convertDateToLocalDateTime(filter.getFromDate()));
        printDTO.setToDate(DateUtils.convertDateToLocalDateTime(filter.getToDate()));
        printDTO.setPrintDate(DateUtils.convertDateToLocalDateTime(new Date()));
        printDTO.setTotalInfo(totalInfo);
        printDTO.setData(dataByCat);
        return printDTO;
    }


    private void removeDataList(List<ReturnGoodsDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size() - 1);
        reportDTOS.remove(reportDTOS.size() - 1);
    }
}
