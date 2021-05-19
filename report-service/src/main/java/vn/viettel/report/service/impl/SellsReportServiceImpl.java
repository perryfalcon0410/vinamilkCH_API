package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ChangeReturnGoodsReportRequest;
import vn.viettel.report.messaging.ReturnGoodsReportsFilter;
import vn.viettel.report.messaging.SellsReportsFilter;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.SellsReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.excel.ReturnGoodsExcel;
import vn.viettel.report.service.excel.SellExcel;
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
public class SellsReportServiceImpl implements SellsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<SellDTO> callStoreProcedure(Long shopId, String orderNumber, Date fromDate, Date toDate, String productKW, Integer collecter, Integer salesChannel, String customerKW, String phoneNumber, Float fromInvoiceSales, Float toInvoiceSales) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_SELL", SellDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(7, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(8, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(9, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(10, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(11, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(12, Integer.class, ParameterMode.IN);

        query.setParameter(2, Integer.valueOf(shopId.toString()));
        query.setParameter(3, orderNumber);
        query.setParameter(4, fromDate);
        query.setParameter(5, toDate);
        query.setParameter(6, productKW);
        query.setParameter(7, collecter);
        query.setParameter(8, salesChannel);
        query.setParameter(9, customerKW);
        query.setParameter(10, phoneNumber);
        query.setParameter(11, fromInvoiceSales);
        query.setParameter(12, toInvoiceSales);

        query.execute();

        List<SellDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    @Override
    public Response<CoverResponse<Page<SellDTO>, SellTotalDTO>> getSellReport(SellsReportsFilter filter, Pageable pageable) {
        List<SellDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getOrderNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductKW(), filter.getCollecter(),
                filter.getSalesChannel(), filter.getCustomerKW(), filter.getPhoneNumber(), filter.getFromInvoiceSales(), filter.getToInvoiceSales());
        SellTotalDTO totalDTO = new SellTotalDTO();
        List<SellDTO> dtoList = new ArrayList<>();

        if (!reportDTOS.isEmpty()) {
            SellDTO dto = reportDTOS.get(reportDTOS.size() - 1);
            totalDTO.setSomeBills(dto.getSomeBills());
            totalDTO.setTotalQuantity(dto.getTotalQuantity());
            totalDTO.setTotalTotal(dto.getTotalTotal());
            totalDTO.setTotalPromotion(dto.getTotalPromotion());
            totalDTO.setTotalPay(dto.getTotalPay());

            this.removeDataList(reportDTOS);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), reportDTOS.size());
            dtoList = reportDTOS.subList(start, end);
        }

        Page<SellDTO> page = new PageImpl<>(dtoList, pageable, reportDTOS.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return new Response<CoverResponse<Page<SellDTO>, SellTotalDTO>>().withData(response);
    }

    @Override
    public ByteArrayInputStream exportExcel(SellsReportsFilter filter) throws IOException {
        List<SellDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getOrderNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductKW(), filter.getCollecter(),
                filter.getSalesChannel(), filter.getCustomerKW(), filter.getPhoneNumber(), filter.getFromInvoiceSales(), filter.getToInvoiceSales());
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        SellDTO sellDTO = new SellDTO();
        if (!reportDTOS.isEmpty()) {
             sellDTO = reportDTOS.get(reportDTOS.size() - 1);
            this.removeDataList(reportDTOS);
        }
        SellExcel excel = new SellExcel(shopDTO,reportDTOS, sellDTO, filter);
        return excel.export();
    }

    //    @Override
//    public Response<CoverResponse<List<ReturnGoodsReportDTO>, ReportTotalDTO>> getDataPrint(ReturnGoodsReportsFilter filter) {
//        List<ReturnGoodsDTO> reportDTOS = this.callStoreProcedure(
//                filter.getShopId(), filter.getReciept(), filter.getFromDate(), filter.getToDate(), filter.getReason(), filter.getProductKW());
//        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
//        ReturnGoodsReportDTO goodsReportDTO = new ReturnGoodsReportDTO(filter.getFromDate(), filter.getToDate(), shopDTO);
//        ReportTotalDTO totalDTO = new ReportTotalDTO();
//        if (!reportDTOS.isEmpty()) {
//            ReturnGoodsDTO dto = reportDTOS.get(reportDTOS.size() - 1);
//            totalDTO.setTotalQuantity(dto.getTotalQuantity());
//            totalDTO.setTotalAmount(dto.getTotalAmount());
//            totalDTO.setTotalRefunds(dto.getTotalRefunds());
//            this.removeDataList(reportDTOS);
//            goodsReportDTO.setFromDate(filter.getFromDate());
//            goodsReportDTO.setToDate(filter.getToDate());
//            goodsReportDTO.setShop(shopDTO);
//            for (int i = 0; i < reportDTOS.size(); i++) {
//
//
//            }
//        }
//        return null;
//    }
//
//
    private void removeDataList(List<SellDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size() - 1);
        reportDTOS.remove(reportDTOS.size() - 1);
    }
}
