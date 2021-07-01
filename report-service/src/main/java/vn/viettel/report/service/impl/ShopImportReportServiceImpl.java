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

        List<PrintShopImportDTO> lstPo = storedProcedure.getResultList();
        List<PrintShopImportDTO> lstBorrow = new ArrayList<>();
        List<PrintShopImportDTO> lstAdjust = new ArrayList<>();
        if(storedProcedure.hasMoreResults())
            lstBorrow = storedProcedure.getResultList();
        if(storedProcedure.hasMoreResults())
            lstAdjust = storedProcedure.getResultList();
        PrintShopImportFilterDTO printDTO = new PrintShopImportFilterDTO(lstPo,lstBorrow,lstAdjust);
        PrintShopImportTotalDTO totalDTO = new PrintShopImportTotalDTO();

        Integer sumQuantity = 0;
        Float sumtotalAmount = 0F;
        if(lstPo.size()>0)
        {
            Integer length = lstPo.size()-1;
            sumQuantity +=lstPo.get(length).getQuantity();
            if(lstPo.get(length).getAmount() != null) {
                sumtotalAmount +=lstPo.get(length).getAmount();
            }

        }
        if(lstBorrow.size()>0)
        {
            Integer length = lstBorrow.size()-1;
            sumQuantity +=lstBorrow.get(length).getQuantity();
            if(lstBorrow.get(length).getAmount() != null) {
                sumtotalAmount += lstBorrow.get(length).getAmount();
            }
        }
        if(lstAdjust.size()>0)
        {
            Integer length = lstAdjust.size()-1;
            sumQuantity +=lstAdjust.get(length).getQuantity();
            if(lstAdjust.get(length).getAmount() != null) {
                sumtotalAmount +=lstAdjust.get(length).getAmount();
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
