package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.feign.UserClient;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.report.messaging.SellsReportsRequest;
import vn.viettel.report.messaging.UserDataResponse;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class SellsReportServiceImpl implements SellsReportService {

    @Autowired
    ShopClient shopClient;

    @Autowired
    UserClient userClient;

    @PersistenceContext
    EntityManager entityManager;

    private List<SellDTO> callStoreProcedure(Long shopId, String orderNumber, LocalDateTime fromDate, LocalDateTime toDate, String productKW, Integer collecter, Integer salesChannel, String customerKW, String phoneNumber, Double fromInvoiceSales, Double toInvoiceSales) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_SELL", SellDTO.class);
        query.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(7, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(8, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(9, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(10, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(11, Double.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(12, Double.class, ParameterMode.IN);

        query.setParameter(2, Integer.valueOf(shopId.toString()));
        query.setParameter(3, orderNumber);
        query.setParameter(4, fromDate);
        query.setParameter(5, toDate);
        query.setParameter(6, productKW);
        query.setParameter(7, collecter);
        query.setParameter(8, salesChannel);
        if (customerKW == null) {
            query.setParameter(9, customerKW);
        } else {
            query.setParameter(9, VNCharacterUtils.removeAccent(customerKW).trim().toUpperCase(Locale.ROOT));
        }
        query.setParameter(10, phoneNumber);
        query.setParameter(11, fromInvoiceSales);
        query.setParameter(12, toInvoiceSales);

        query.execute();

        List<SellDTO> reportDTOS = query.getResultList();
        entityManager.close();
        return reportDTOS;
    }

    @Override
    public CoverResponse<Page<SellDTO>, SellTotalDTO> getSellReport(SellsReportsRequest filter, Pageable pageable) {
        if (filter.getFromInvoiceSales() != null && filter.getToInvoiceSales() != null) {
            if (filter.getFromInvoiceSales() > filter.getToInvoiceSales())
                throw new ValidateException(ResponseMessage.SALES_FROM_CANNOT_BE_GREATER_THAN_SALES_TO);
        }
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
            totalDTO.setTotalPromotionNotVAT(dto.getTotalPromotionNotVAT());
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
        if (filter.getFromInvoiceSales() != null || filter.getToInvoiceSales() != null) {
            if (filter.getFromInvoiceSales() > filter.getToInvoiceSales())
                throw new ValidateException(ResponseMessage.SALES_FROM_CANNOT_BE_GREATER_THAN_SALES_TO);
        }
        List<SellDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getOrderNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductKW(), filter.getCollecter(),
                filter.getSalesChannel(), filter.getCustomerKW(), filter.getPhoneNumber(), filter.getFromInvoiceSales(), filter.getToInvoiceSales());
        if (reportDTOS.size() == 0)
            throw new ValidateException(ResponseMessage.SELL_REPORT_NOT_FOUND);
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        SellDTO sellDTO = new SellDTO();
        if (!reportDTOS.isEmpty()) {
            sellDTO = reportDTOS.get(reportDTOS.size() - 1);
            this.removeDataList(reportDTOS);
        }
        SellExcel excel = new SellExcel(shopDTO, shopDTO.getParentShop(), reportDTOS, sellDTO, filter);
        return excel.export();
    }

    @Override
    public CoverResponse<List<SellDTO>, ReportDateDTO> getDataPrint(SellsReportsRequest filter) {
        if (filter.getFromInvoiceSales() != null || filter.getToInvoiceSales() != null) {
            if (filter.getFromInvoiceSales() > filter.getToInvoiceSales())
                throw new ValidateException(ResponseMessage.SALES_FROM_CANNOT_BE_GREATER_THAN_SALES_TO);
        }
        List<SellDTO> reportDTOS = this.callStoreProcedure(
                filter.getShopId(), filter.getOrderNumber(), filter.getFromDate(), filter.getToDate(), filter.getProductKW(), filter.getCollecter(),
                filter.getSalesChannel(), filter.getCustomerKW(), filter.getPhoneNumber(), filter.getFromInvoiceSales(), filter.getToInvoiceSales());
        if (reportDTOS.size() == 0)
            throw new ValidateException(ResponseMessage.SELL_REPORT_NOT_FOUND);
        ReportSellDTO dto = new ReportSellDTO();

        if (!reportDTOS.isEmpty()) {
            SellDTO sellDTO = reportDTOS.get(reportDTOS.size() - 1);
            ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
            if(shopDTO == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
            dto.setFromDate(filter.getFromDate());
            dto.setToDate(filter.getToDate());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy -HH:mm:ss Z");
            String time = ZonedDateTime.now().format(dateTimeFormatter);
            dto.setDateOfPrinting(time);
            dto.setShopName(shopDTO.getShopName());
            dto.setAddress(shopDTO.getAddress());
            dto.setTel(shopDTO.getPhone());
            dto.setSomeBills(sellDTO.getSomeBills());
            dto.setTotalQuantity(sellDTO.getTotalQuantity());
            dto.setTotalTotal(sellDTO.getTotalTotal());
            dto.setTotalPromotionNotVat(sellDTO.getTotalPromotionNotVAT());
            dto.setTotalPromotion(sellDTO.getTotalPromotion());
            dto.setTotalPay(sellDTO.getTotalPay());
            this.removeDataList(reportDTOS);

        }
        CoverResponse response = new CoverResponse(reportDTOS, dto);
        return response;
    }

    @Override
    public List<UserDataResponse> getDataUser(Long shopId) {
        List<UserDTO> dtoList = userClient.getUserDataV1(shopId);
        List<UserDataResponse> list = new ArrayList<>();
        for (UserDTO userDTO : dtoList) {
            UserDataResponse response = new UserDataResponse();
            response.setId(userDTO.getId());
            response.setFullName(userDTO.getLastName() + " " + userDTO.getFirstName());
            list.add(response);
        }

        return list;
    }

    private void removeDataList(List<SellDTO> reportDTOS) {
        reportDTOS.remove(reportDTOS.size() - 1);
        reportDTOS.remove(reportDTOS.size() - 1);
    }
}
