package vn.viettel.report.service.impl;

import liquibase.pro.packaged.D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShopImportReportServiceImpl implements ShopImportReportService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    ShopClient shopClient;
    @Override
    public CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO> find(ShopImportFilter filter, Pageable pageable) {
        if(filter.getImportType() !=null){
            if(filter.getImportType().equals("0")) filter.setImportType("0,3");
        }
        List<ShopImportDTO> shopImports =  this.callProcedure(filter).getData();
        ShopImportTotalDTO totalDTO = new ShopImportTotalDTO();
        List<ShopImportDTO> subList = new ArrayList<>();
        if(!shopImports.isEmpty()) {
            ShopImportDTO total = shopImports.get(shopImports.size() -1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalWholeSale(total.getWholesale());
            totalDTO.setTotalRetail(total.getRetail());
            totalDTO.setTotalAmount(total.getAmount());
            totalDTO.setTotal(total.getTotal());
            this.removeDataList(shopImports);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), shopImports.size());
            subList = shopImports.subList(start, end);
        }
        Page<ShopImportDTO> page = new PageImpl<>( subList, pageable, shopImports.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }
    @Override
    public  Response<List<ShopImportDTO>> callProcedure(ShopImportFilter filter) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_SHOP_IMPORT", ShopImportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, Date.class, ParameterMode.IN);
        ///////////////////////////////////////////////////////////////////////////////////////////
        storedProcedure.setParameter(2, filter.getFromDate());
        storedProcedure.setParameter(3, filter.getToDate());
        storedProcedure.setParameter(4, filter.getProductCodes());
        storedProcedure.setParameter(5, filter.getImportType());
        storedProcedure.setParameter(6, filter.getInternalNumber());
        storedProcedure.setParameter(7, filter.getFromOrderDate());
        storedProcedure.setParameter(8, filter.getToOrderDate());
        storedProcedure.execute();
        return new Response<List<ShopImportDTO>>().withData(storedProcedure.getResultList());
    }

    @Override
    public  Response<CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO>> dataExcel(ShopImportFilter filter) {
        List<ShopImportDTO> shopImports =  this.callProcedure(filter).getData();
        ShopImportTotalDTO totalDTO = new ShopImportTotalDTO();
        if(!shopImports.isEmpty()) {
            ShopImportDTO total = shopImports.get(shopImports.size() -1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalWholeSale(total.getWholesale());
            totalDTO.setTotalRetail(total.getRetail());
            totalDTO.setTotalAmount(total.getAmount());
            totalDTO.setTotal(total.getTotal());
            this.removeDataList(shopImports);
        }
        CoverResponse response = new CoverResponse(shopImports, totalDTO);
        return new Response<CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO>>().withData(response);
    }

    @Override
    public CoverResponse<PrintShopImportFilterDTO, PrintShopImportTotalDTO> print(ShopImportFilter filter, Long shopId) {
        if(filter.getImportType() !=null){
            if(filter.getImportType().equals("0")) filter.setImportType("0,3");
        }
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("P_PRINT_SHOP_IMPORT",PrintShopImportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(3, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(9, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(10,Date.class, ParameterMode.IN);
        ///////////////////////////////////////////////////////////////////////////////////////////
        storedProcedure.setParameter(4, filter.getFromDate());
        storedProcedure.setParameter(5, filter.getToDate());
        storedProcedure.setParameter(6, filter.getProductCodes().trim());
        storedProcedure.setParameter(7, filter.getImportType());
        storedProcedure.setParameter(8, filter.getInternalNumber().trim());
        storedProcedure.setParameter(9, filter.getFromOrderDate());
        storedProcedure.setParameter(10, filter.getToOrderDate());
        storedProcedure.execute();

        List<PrintShopImportDTO> dataPo = storedProcedure.getResultList();
        List<PrintShopImportDTO> dataBorrow = new ArrayList<>();
        List<PrintShopImportDTO> dataAdjust = new ArrayList<>();
        if(storedProcedure.hasMoreResults())
            dataBorrow = storedProcedure.getResultList();
        if(storedProcedure.hasMoreResults())
            dataAdjust = storedProcedure.getResultList();
        PrintShopImportTotalDTO totalDTO = new PrintShopImportTotalDTO();

        List<orderImportDTO> PO = new ArrayList<>();
        Map<Long, List<PrintShopImportDTO>> lstPO = dataPo.subList(0, dataPo.size() - 2).stream().collect(Collectors.groupingBy(PrintShopImportDTO::getOrderId));
        for(Map.Entry<Long, List<PrintShopImportDTO>> value:lstPO.entrySet()) {
            orderImportDTO orderPO = new orderImportDTO();
            Integer orderQuantity = 0;
            Double orderTotal = 0.0;
            for (PrintShopImportDTO infoPo:value.getValue()){
                orderPO.setOrderNumber(infoPo.getRedInvoiceNo());
                orderPO.setOrderDate(infoPo.getOrderDate());
                orderPO.setPoNumber(infoPo.getPoNumber());
                orderPO.setInternalNumber(infoPo.getInternalNumber());
                orderPO.setImportNumber(infoPo.getTransCode());
                orderPO.setDataPO(lstPO.get(value.getKey()));
                orderQuantity += infoPo.getQuantity();
                orderTotal += infoPo.getTotal();
            }
            orderPO.setOrderQuantity(orderQuantity);
            orderPO.setOrderTotal(orderTotal);
            PO.add(orderPO);
        }

        List<orderImportDTO> Borrow = new ArrayList<>();
        Map<Long, List<PrintShopImportDTO>> lstBorrow = dataBorrow.subList(0, dataBorrow.size() - 2).stream().collect(Collectors.groupingBy(PrintShopImportDTO::getOrderId));
        for(Map.Entry<Long, List<PrintShopImportDTO>> value:lstBorrow.entrySet()) {
            orderImportDTO orderBorrow = new orderImportDTO();
            Integer orderQuantity = 0;
            Double orderTotal = 0.0;
            for (PrintShopImportDTO infoBorrow:value.getValue()){
                orderBorrow.setOrderNumber(infoBorrow.getRedInvoiceNo());
                orderBorrow.setOrderDate(infoBorrow.getOrderDate());
                orderBorrow.setPoNumber(infoBorrow.getPoNumber());
                orderBorrow.setInternalNumber(infoBorrow.getInternalNumber());
                orderBorrow.setImportNumber(infoBorrow.getTransCode());
                orderBorrow.setDataPO(lstBorrow.get(value.getKey()));
                orderQuantity += infoBorrow.getQuantity();
                orderTotal += infoBorrow.getTotal();
            }
            orderBorrow.setOrderQuantity(orderQuantity);
            orderBorrow.setOrderTotal(orderTotal);
            Borrow.add(orderBorrow);
        }

        List<orderImportDTO> Adjust = new ArrayList<>();
        Map<Long, List<PrintShopImportDTO>> lstAdjust = dataAdjust.subList(0, dataAdjust.size() - 2).stream().collect(Collectors.groupingBy(PrintShopImportDTO::getOrderId));
        for(Map.Entry<Long, List<PrintShopImportDTO>> value:lstAdjust.entrySet()) {
            orderImportDTO orderAdjust = new orderImportDTO();
            Integer orderQuantity = 0;
            Double orderTotal = 0.0;
            for (PrintShopImportDTO infoAdjust:value.getValue()){
                orderAdjust.setOrderNumber(infoAdjust.getRedInvoiceNo());
                orderAdjust.setOrderDate(infoAdjust.getOrderDate());
                orderAdjust.setPoNumber(infoAdjust.getPoNumber());
                orderAdjust.setInternalNumber(infoAdjust.getInternalNumber());
                orderAdjust.setImportNumber(infoAdjust.getTransCode());
                orderAdjust.setDataPO(lstAdjust.get(value.getKey()));
                orderQuantity += infoAdjust.getQuantity();
                orderTotal += infoAdjust.getTotal();
            }
            orderAdjust.setOrderQuantity(orderQuantity);
            orderAdjust.setOrderTotal(orderTotal);
            Adjust.add(orderAdjust);
        }

        Integer sumQuantity = 0;
        Float sumtotalAmount = 0F;
        if(dataPo.size()>0)
        {
            Integer length = dataPo.size()-1;
            sumQuantity +=dataPo.get(length).getQuantity();
            if(dataPo.get(length).getAmount() != null) {
                sumtotalAmount +=dataPo.get(length).getAmount();
            }

        }
        if(dataBorrow.size()>0)
        {
            Integer length = dataBorrow.size()-1;
            sumQuantity +=dataBorrow.get(length).getQuantity();
            if(dataBorrow.get(length).getAmount() != null) {
                sumtotalAmount += dataBorrow.get(length).getAmount();
            }
        }
        if(dataAdjust.size()>0)
        {
            Integer length = dataAdjust.size()-1;
            sumQuantity +=dataAdjust.get(length).getQuantity();
            if(dataAdjust.get(length).getAmount() != null) {
                sumtotalAmount +=dataAdjust.get(length).getAmount();
            }
        }
        totalDTO.setTotalQuantity(sumQuantity);
        totalDTO.setTotalAmount(sumtotalAmount);
        ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();
        if(shopDTO != null){
            totalDTO.setShopName(shopDTO.getShopName());
            totalDTO.setShopAddress(shopDTO.getAddress());
            totalDTO.setShopPhone(shopDTO.getMobiPhone());
        }
        PrintShopImportFilterDTO printDTO = new PrintShopImportFilterDTO(PO,Borrow,Adjust);
        totalDTO.setToDate(DateUtils.convertDateToLocalDateTime(filter.getToDate()));
        totalDTO.setFromDate(DateUtils.convertDateToLocalDateTime(filter.getFromDate()));
        totalDTO.setPrintDate(DateUtils.convertDateToLocalDateTime(new Date()));
        CoverResponse<PrintShopImportFilterDTO, PrintShopImportTotalDTO> response = new CoverResponse<>(printDTO,totalDTO);
        return response;
    }

    private void removeDataList(List<ShopImportDTO> shopImports) {
        shopImports.remove(shopImports.size()-1);
        shopImports.remove(shopImports.size()-1);
    }
}
