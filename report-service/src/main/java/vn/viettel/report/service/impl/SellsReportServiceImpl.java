package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.SellsReportsRequest;
import vn.viettel.report.service.SellsReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.excel.SellExcel;
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

@Service
public class SellsReportServiceImpl implements SellsReportService {

    @Autowired
    ShopClient shopClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<SellDTO> callStoreProcedure(Long shopId, String orderNumber, LocalDate fromDate, LocalDate toDate, String productKW, Integer collecter, Integer salesChannel, String customerKW, String phoneNumber, Float fromInvoiceSales, Float toInvoiceSales) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_SELL", SellDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, LocalDate.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, LocalDate.class, ParameterMode.IN);
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
    public CoverResponse<Page<SellDTO>, SellTotalDTO> getSellReport(SellsReportsRequest filter, Pageable pageable) {
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
        return response;
    }

    @Override
    public ByteArrayInputStream exportExcel(SellsReportsRequest filter) throws IOException {
        List<SellDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getOrderNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductKW(), filter.getCollecter(),
                filter.getSalesChannel(), filter.getCustomerKW(), filter.getPhoneNumber(), filter.getFromInvoiceSales(), filter.getToInvoiceSales());
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        SellDTO sellDTO = new SellDTO();
        if (!reportDTOS.isEmpty()) {
            sellDTO = reportDTOS.get(reportDTOS.size() - 1);
            this.removeDataList(reportDTOS);
        }
        SellExcel excel = new SellExcel(shopDTO, reportDTOS, sellDTO, filter);
        return excel.export();
    }

    @Override
    public CoverResponse<List<SellDTO>, ReportDateDTO> getDataPrint(SellsReportsRequest filter) {
        List<SellDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getOrderNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductKW(), filter.getCollecter(),
                filter.getSalesChannel(), filter.getCustomerKW(), filter.getPhoneNumber(), filter.getFromInvoiceSales(), filter.getToInvoiceSales());
        ReportSellDTO dto = new ReportSellDTO();

        if (!reportDTOS.isEmpty()) {
            SellDTO sellDTO = reportDTOS.get(reportDTOS.size() - 1);
            ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
            dto.setFromDate(filter.getFromDate());
            dto.setToDate(filter.getToDate());
            String dateOfPrinting = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy -HH:mm:ss Z"));
            dto.setDateOfPrinting(dateOfPrinting);
            dto.setShopName(shopDTO.getShopName());
            dto.setAddress(shopDTO.getAddress());
            dto.setTel(shopDTO.getPhone());
            dto.setSomeBills(sellDTO.getSomeBills());
            dto.setTotalQuantity(sellDTO.getTotalQuantity());
            dto.setTotalTotal(sellDTO.getTotalTotal());
            dto.setTotalPromotion(sellDTO.getTotalPromotion());
            dto.setTotalPay(sellDTO.getTotalPay());
            this.removeDataList(reportDTOS);

        }
        CoverResponse response = new CoverResponse(reportDTOS, dto);
        return response;
    }

    private void removeDataList(List<SellDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size() - 1);
        reportDTOS.remove(reportDTOS.size() - 1);
    }
}
