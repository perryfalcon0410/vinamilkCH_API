package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.ShopExportFilter;
import vn.viettel.report.messaging.PrintGoodFilter;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ShopExportDTO;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportExportGoodsServiceImpl implements ReportExportGoodsService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public CoverResponse<Page<ShopExportDTO>, TotalReport> index(ShopExportFilter filter, Pageable pageable) {
        List<ShopExportDTO> shopExports =  this.callProcedure(filter);
        TotalReport totalDTO = new TotalReport();
        List<ShopExportDTO> subList = new ArrayList<>();
        if(!shopExports.isEmpty()) {
            ShopExportDTO total = shopExports.get(shopExports.size() -1);
            totalDTO.setTotalQuantity(total.getQuantity());
            totalDTO.setTotalPacketQuantity(total.getWholesale());
            totalDTO.setTotalUnitQuantity(total.getRetail());
            totalDTO.setTotalAmountNotVat(total.getTotalPriceNotVat());
            totalDTO.setTotalAmountVat(total.getTotalPriceVat());

            this.removeDataList(shopExports);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), shopExports.size());
            subList = shopExports.subList(start, end);
        }
        Page<ShopExportDTO> page = new PageImpl<>( subList, pageable, shopExports.size());
        CoverResponse response = new CoverResponse(page, totalDTO);
        return response;
    }

    @Override
    public ByteArrayInputStream exportExcel(ShopExportFilter shopExportFilter) throws IOException {


        return null;
    }

    @Override
    public CoverResponse<PrintGoodFilter, TotalReport> getDataToPrint(ShopExportFilter filter) {
        return null;
    }


    private List<ShopExportDTO> callProcedure(ShopExportFilter filter) {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_SHOP_EXPORT", ShopExportDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, LocalDateTime.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(9, Long.class, ParameterMode.IN);
        ///////////////////////////////////////////////////////////////////////////////////////////
        storedProcedure.setParameter(2, filter.getFromDate());
        storedProcedure.setParameter(3, filter.getToDate());
        storedProcedure.setParameter(4, filter.getProductCodes());
        storedProcedure.setParameter(5, filter.getImportType());
        storedProcedure.setParameter(6, filter.getSearchKeywords());
        storedProcedure.setParameter(7, filter.getFromOrderDate());
        storedProcedure.setParameter(8, filter.getToOrderDate());
        storedProcedure.setParameter(9, filter.getShopId());
        storedProcedure.execute();
        return storedProcedure.getResultList();
    }

    private void removeDataList(List<ShopExportDTO> shopExports) {
        shopExports.remove(shopExports.size()-1);
        shopExports.remove(shopExports.size()-1);
    }



//    @Override
//    public CoverResponse<Page<ShopExportDTO>, TotalReport> index(ShopExportFilter filter, Pageable pageable) {
//        StoredProcedureQuery storedProcedure = callPExportGoods(filter);
//        List<ShopExportDTO> lst = storedProcedure.getResultList();
//
//        TotalReport totalReport = new TotalReport();
//        List<ShopExportDTO> subList = new ArrayList<>();
//        if(!lst.isEmpty())
//        {
//            Integer l = lst.size();
//            ShopExportDTO lastExportGood = lst.get(l-1);
//            totalReport.setTotalAmount(lastExportGood.getTotalAmount());
//            totalReport.setTotalQuantity(lastExportGood.getQuantity());
//            totalReport.setTotalAmountNotVat(lastExportGood.getAmountNotVat());
//            totalReport.setTotalPacketQuantity(lastExportGood.getPacketQuantity());
//            totalReport.setTotalUnitQuantity(lastExportGood.getUnitQuantity());
//
//            lst.remove(l-1);
//            lst.remove(l-2);
//            int start = (int)pageable.getOffset();
//            int end = Math.min((start + pageable.getPageSize()), lst.size());
//            subList = lst.subList(start, end);
//        }
//        Page<ShopExportDTO>  page = new PageImpl<>(subList, pageable, lst.size());
//
//        return new CoverResponse(page,totalReport);
//    }
//
//    @Override
//    public ByteArrayInputStream exportExcel(ShopExportFilter filter) throws IOException {
//        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
//        CoverResponse<List<ShopExportDTO>, TotalReport> coverResponse = this.getDataExport(filter);
//        ExportGoodsExcel excel = new ExportGoodsExcel(shopDTO, coverResponse.getResponse(),coverResponse.getInfo());
//        excel.setFromDate(filter.getFromExportDate());
//        excel.setToDate(filter.getToExportDate());
//        return excel.export();
//    }
//
//    @Override
//    public CoverResponse<PrintGoodFilter, TotalReport> getDataToPrint(ShopExportFilter filter) {
//        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("P_PRINT_GOODS",PrintGoodDTO.class)
//        .registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR)
//        .registerStoredProcedureParameter(2, LocalDate.class, ParameterMode.IN).setParameter(2, filter.getFromExportDate())
//        .registerStoredProcedureParameter(3, LocalDate.class, ParameterMode.IN).setParameter(3, filter.getToExportDate())
//        .registerStoredProcedureParameter(4, LocalDate.class, ParameterMode.IN).setParameter(4, filter.getFromOrderDate())
//        .registerStoredProcedureParameter(5, LocalDate.class, ParameterMode.IN).setParameter(5, filter.getToOrderDate())
//        .registerStoredProcedureParameter(6, String.class, ParameterMode.IN).setParameter(6, filter.getLstProduct())
//        .registerStoredProcedureParameter(7, String.class, ParameterMode.IN).setParameter(7, filter.getLstExportType())
//        .registerStoredProcedureParameter(8, String.class, ParameterMode.IN).setParameter(8, filter.getSearchKeywords())
//        .registerStoredProcedureParameter(9, Long.class, ParameterMode.IN).setParameter(9, filter.getShopId())
//        .registerStoredProcedureParameter(10, void.class, ParameterMode.REF_CURSOR)
//        .registerStoredProcedureParameter(11, void.class, ParameterMode.REF_CURSOR);
//
//        storedProcedure.execute();
//
//        List<PrintGoodDTO> lstAdjust = storedProcedure.getResultList();
//        List<PrintGoodDTO> lstPo = new ArrayList<>();
//        List<PrintGoodDTO> lstStock = new ArrayList<>();
//        if(storedProcedure.hasMoreResults())
//            lstPo = storedProcedure.getResultList();
//        if(storedProcedure.hasMoreResults())
//            lstStock = storedProcedure.getResultList();
//
//        PrintGoodFilter printGoodFilter = new PrintGoodFilter(lstAdjust,lstPo,lstStock);
//        TotalReport totalReport = new TotalReport();
//
//        Integer sumQuantity = 0;
//        Float sumtotalAmount = 0F;
//        if(lstAdjust.size()>0)
//        {
//            Integer length = lstAdjust.size()-1;
//            sumQuantity +=lstAdjust.get(length).getQuantity();
//            sumtotalAmount +=lstAdjust.get(length).getTotalAmount();
//        }
//        if(lstPo.size()>0)
//        {
//            Integer length = lstPo.size()-1;
//            sumQuantity +=lstPo.get(length).getQuantity();
//            sumtotalAmount +=lstPo.get(length).getTotalAmount();
//        }
//        if(lstStock.size()>0)
//        {
//            Integer length = lstStock.size()-1;
//            sumQuantity +=lstStock.get(length).getQuantity();
//            sumtotalAmount +=lstStock.get(length).getTotalAmount();
//        }
//
//        totalReport.setTotalQuantity(sumQuantity);
//        totalReport.setTotalAmount(sumtotalAmount);
//
//        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
//        if(shopDTO != null){
//            totalReport.setShopName(shopDTO.getShopName());
//            totalReport.setShopAddress(shopDTO.getAddress());
//            totalReport.setShopPhone(shopDTO.getMobiPhone());
//        }
//        totalReport.setToDate(filter.getToExportDate());
//        totalReport.setFromDate(filter.getFromExportDate());
//
//        CoverResponse<PrintGoodFilter, TotalReport> coverResponse = new CoverResponse<>(printGoodFilter, totalReport);
//
//        return coverResponse;
//    }
//
//    public CoverResponse<List<ShopExportDTO>, TotalReport> getDataExport(ShopExportFilter filter)
//    {
//        StoredProcedureQuery storedProcedure = callPExportGoods(filter);
//        List<ShopExportDTO> lst = storedProcedure.getResultList();
//        TotalReport totalReport = new TotalReport();
//        if(lst.size() > 0)
//        {
//            Integer l = lst.size();
//            ShopExportDTO lastExportGood = lst.get(l-1);
//            totalReport.setTotalAmount(lastExportGood.getTotalAmount());
//            totalReport.setTotalQuantity(lastExportGood.getQuantity());
//            totalReport.setTotalAmountNotVat(lastExportGood.getAmountNotVat());
//            totalReport.setTotalPacketQuantity(lastExportGood.getPacketQuantity());
//            totalReport.setTotalUnitQuantity(lastExportGood.getUnitQuantity());
//            totalReport.setTotalPrice(lastExportGood.getPrice());
//            lst.remove(l-1);
//            lst.remove(l-2);
//        }
//
//        return new CoverResponse(lst, totalReport);
//    }
//
//    public StoredProcedureQuery callPExportGoods(ShopExportFilter filter)
//    {
//        StoredProcedureQuery storedProcedure =
//                entityManager.createStoredProcedureQuery("P_EXPORT_GOODS", ShopExportDTO.class);
//        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
//        storedProcedure.registerStoredProcedureParameter(2, LocalDate.class, ParameterMode.IN);
//        storedProcedure.registerStoredProcedureParameter(3, LocalDate.class, ParameterMode.IN);
//        storedProcedure.registerStoredProcedureParameter(4, LocalDate.class, ParameterMode.IN);
//        storedProcedure.registerStoredProcedureParameter(5, LocalDate.class, ParameterMode.IN);
//        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
//        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
//        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);
//        storedProcedure.registerStoredProcedureParameter(9, Long.class, ParameterMode.IN);
//
//        storedProcedure.setParameter(2, filter.getFromExportDate());
//        storedProcedure.setParameter(3, filter.getToExportDate());
//        storedProcedure.setParameter(4, filter.getFromOrderDate());
//        storedProcedure.setParameter(5, filter.getToOrderDate());
//        storedProcedure.setParameter(6, VNCharacterUtils.removeAccent(filter.getLstProduct()).trim().toUpperCase(Locale.ROOT));
//        storedProcedure.setParameter(7, filter.getLstExportType());
//        storedProcedure.setParameter(8, VNCharacterUtils.removeAccent(filter.getSearchKeywords()).trim().toUpperCase(Locale.ROOT));
//        storedProcedure.setParameter(9, filter.getShopId());
//        storedProcedure.execute();
//        return storedProcedure;
//    }
}
