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
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReturnGoodsReportServiceImpl implements ReturnGoodsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<ReturnGoodsDTO> callStoreProcedure(ReturnGoodsReportsRequest filter) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_RETURNED_GOODS", ReturnGoodsDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);

        query.setParameter(2, filter.getShopId());
        query.setParameter(3, filter.getReciept());
        query.setParameter(4, filter.getFromDate());
        query.setParameter(5, filter.getToDate());
        query.setParameter(6, filter.getReason());
        query.setParameter(7, filter.getProductKW());

        query.execute();

        List<ReturnGoodsDTO> reportDTOS = query.getResultList();
        entityManager.close();
        return reportDTOS;
    }

    @Override
    public CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO> getReturnGoodsReport(ReturnGoodsReportsRequest filter, Pageable pageable) {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(filter);
        ReportTotalDTO totalDTO = new ReportTotalDTO();
        List<ReturnGoodsDTO> dtoList = new ArrayList<>();

        if (!reportDTOS.isEmpty()) {
            ReturnGoodsDTO dto = reportDTOS.get(reportDTOS.size() - 1);
            totalDTO.setTotalQuantity(dto.getTotalQuantity());
            totalDTO.setTotalAmount(dto.getTotalAmount().floatValue());
            totalDTO.setTotalRefunds(dto.getTotalRefunds().floatValue());

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
    public ByteArrayInputStream exportExcel(ReturnGoodsReportsRequest filter) throws IOException, ParseException {
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(filter);
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
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
        ReturnGoodsExcel excel = new ReturnGoodsExcel(shopDTO, shopDTO.getParentShop(), reportRequest, filter);
        return excel.export();
    }


    @Override
    public ReportPrintIndustryTotalDTO getDataPrint(ReturnGoodsReportsRequest filter) {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        ReportPrintIndustryTotalDTO printDTO = new ReportPrintIndustryTotalDTO();
        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(filter);
        ReturnGoodsDTO totalInfo = reportDTOS.get(reportDTOS.size() - 1);
        List<ReturnGoodsDTO> listResults = reportDTOS.subList(0, reportDTOS.size() - 2);
        Map<Long, List<ReturnGoodsDTO>> cats = listResults.stream().collect(Collectors.groupingBy(ReturnGoodsDTO::getIndustryId));
        List<ReportPrintOrderTotalDTO> dataByCat = new ArrayList<>();
        printDTO.setTotalInfo(totalInfo);
        for (Map.Entry<Long, List<ReturnGoodsDTO>> entry : cats.entrySet()) {
            ReportPrintOrderTotalDTO cat = new ReportPrintOrderTotalDTO();
            Integer totalQuantity = 0;
            Double totalAmount = 0.0;
            Double totalRefunds = 0.0;
            for(ReturnGoodsDTO total : entry.getValue()) {
                cat.setCategory(total.getIndustry());
                totalQuantity += total.getQuantity()!=null?total.getQuantity():0;
                totalAmount += total.getAmount()!=null?total.getAmount():0.0;
                totalRefunds +=total.getRefunds()!=null?total.getRefunds():0.0;
            }
            cat.setTotalQuantity(totalQuantity);
            cat.setTotalAmount(totalAmount);
            cat.setTotalRefunds(totalRefunds);

            Map<Long, List<ReturnGoodsDTO>> lstIds = entry.getValue().stream().collect(Collectors.groupingBy(ReturnGoodsDTO::getReturnId));
            List<OrderReturnGoodsReportDTO> lstOrderReturn = new ArrayList<>();
            for(Map.Entry<Long, List<ReturnGoodsDTO>> value:lstIds.entrySet()){
            OrderReturnGoodsReportDTO orderReturn = new OrderReturnGoodsReportDTO();
            Integer orderQuantity = 0;
            Double orderAmount = 0.0;
            Double orderRefund = 0.0;
                for(ReturnGoodsDTO info:value.getValue()){
                    orderReturn.setReturnNumber(info.getReturnCode());
                    orderReturn.setOrderNumber(info.getReciept());
                    orderReturn.setCustomerName(info.getFullName());
                    orderReturn.setReportPrintProductDTOS(lstIds.get(value.getKey()));
                    orderQuantity += info.getQuantity();
                    orderAmount += info.getAmount();
                    orderRefund += info.getRefunds();
                }
                orderReturn.setOrderQuantity(orderQuantity);
                orderReturn.setOrderAmount(orderAmount);
                orderReturn.setOrderRefund(orderRefund);
                lstOrderReturn.add(orderReturn);
            }
            cat.setOrderReturnGoods(lstOrderReturn);
            dataByCat.add(cat);
        }
        Collections.sort(dataByCat, Comparator.comparing(ReportPrintOrderTotalDTO::getCategory));

        printDTO.setShopName(shopDTO.getShopName());
        printDTO.setShopTel(shopDTO.getMobiPhone());
        printDTO.setAddress(shopDTO.getAddress());
        printDTO.setFromDate(filter.getFromDate());
        printDTO.setToDate(filter.getToDate());
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
