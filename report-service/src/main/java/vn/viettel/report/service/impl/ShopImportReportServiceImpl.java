package vn.viettel.report.service.impl;

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
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.*;
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
    public PrintShopImportFilterDTO print(ShopImportFilter filter, Long shopId) {
        if(filter.getImportType() !=null){
            if(filter.getImportType().equals("0")) filter.setImportType("0,3");
        }
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("P_SHOP_IMPORT_2",PrintShopImportDTO.class);
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

        PrintShopImportFilterDTO printDTO = new PrintShopImportFilterDTO();
        ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();

        List<PrintShopImportDTO> dataPo = storedProcedure.getResultList();
        PrintShopImportDTO totalInfo = dataPo.get(dataPo.size() - 1);
        List<PrintShopImportDTO> listResults = dataPo.subList(0, dataPo.size() - 2);
        Map<Long, List<PrintShopImportDTO>> typess = listResults.stream().collect(Collectors.groupingBy(PrintShopImportDTO::getTypess));
        List<PrintShopImportTotalDTO> dataByType = new ArrayList<>();
        printDTO.setTotalInfo(totalInfo);
        for (Map.Entry<Long, List<PrintShopImportDTO>> entry : typess.entrySet()) {
            PrintShopImportTotalDTO type = new PrintShopImportTotalDTO();
            Integer totalQuantity = 0;
            Float totalAmount = 0F;
            for(PrintShopImportDTO total : entry.getValue()) {
                type.setType(total.getImportType());
                totalQuantity += total.getQuantity();
                if(total.getAmount()!=null) {
                    totalAmount += total.getAmount();
                }
            }
            type.setTotalQuantity(totalQuantity);
            type.setTotalAmount(totalAmount);
            Map<Long, List<PrintShopImportDTO>> lstIds = listResults.stream().collect(Collectors.groupingBy(PrintShopImportDTO::getOrderId));
            List<orderImportDTO> lstOrderImport = new ArrayList<>();
            for(Map.Entry<Long, List<PrintShopImportDTO>> value:lstIds.entrySet()){
                orderImportDTO orderImport = new orderImportDTO();
                Integer orderQuantity = 0;
                Double orderAmount = 0.0;
                for(PrintShopImportDTO info:value.getValue()){
                    orderImport.setOrderNumber(info.getRedInvoiceNo());
                    orderImport.setOrderDate(info.getOrderDate());
                    orderImport.setPoNumber(info.getPoNumber());
                    orderImport.setInternalNumber(info.getInternalNumber());
                    orderImport.setImportNumber(info.getTransCode());
                    orderQuantity += info.getQuantity();
                    if(info.getAmount()!=null){
                        orderAmount += info.getAmount();
                    }
                    orderImport.setDataPO(lstIds.get(value.getKey()));
                }
                orderImport.setOrderQuantity(orderQuantity);
                orderImport.setOrderTotal(orderAmount);
                lstOrderImport.add(orderImport);
            }
            type.setOrderImport(lstOrderImport);
            dataByType.add(type);
        }
        printDTO.setShopName(shopDTO.getShopName());
        printDTO.setShopTel(shopDTO.getMobiPhone());
        printDTO.setAddress(shopDTO.getAddress());
        printDTO.setFromDate(DateUtils.convertDateToLocalDateTime(filter.getFromDate()));
        printDTO.setToDate(DateUtils.convertDateToLocalDateTime(filter.getToDate()));
        printDTO.setPrintDate(DateUtils.convertDateToLocalDateTime(new Date()));
        printDTO.setTotalInfo(totalInfo);
        printDTO.setDataType(dataByType);
        return printDTO;
    }

    private void removeDataList(List<ShopImportDTO> shopImports) {
        shopImports.remove(shopImports.size()-1);
        shopImports.remove(shopImports.size()-1);
    }
}
