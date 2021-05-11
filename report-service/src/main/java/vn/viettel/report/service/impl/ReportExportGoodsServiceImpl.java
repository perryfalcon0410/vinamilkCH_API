package vn.viettel.report.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ExportGoodFilter;
import vn.viettel.report.messaging.PrintGoods;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ExportGoodsDTO;
import vn.viettel.report.service.dto.PrintGoodDTO;
import vn.viettel.report.service.excel.ExportGoodsExcel;
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
public class ReportExportGoodsServiceImpl implements ReportExportGoodsService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>> index(ExportGoodFilter filter, Pageable pageable) {
        StoredProcedureQuery storedProcedure = callPExportGoods(filter);
        List<ExportGoodsDTO> lst = storedProcedure.getResultList();
        TotalReport totalReport = new TotalReport();
        List<ExportGoodsDTO> subList = new ArrayList<>();
        if(!lst.isEmpty())
        {
            Integer l = lst.size();
            ExportGoodsDTO lastExportGood = lst.get(l-1);
            totalReport.setTotalAmount(lastExportGood.getTotalAmount());
            totalReport.setTotalQuantity(lastExportGood.getQuantity());
            totalReport.setTotalAmountNotVat(lastExportGood.getAmountNotVat());
            totalReport.setTotalPacketQuantity(lastExportGood.getPacketQuantity());
            totalReport.setTotalUnitQuantity(lastExportGood.getUnitQuantity());

            lst.remove(l-1);
            lst.remove(l-2);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), lst.size());
            subList = lst.subList(start, end);
        }

        Page<ExportGoodsDTO>  page = new PageImpl<>(subList, pageable, lst.size());

        return new Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>>().withData(new CoverResponse(page,totalReport));
    }

    @Override
    public ByteArrayInputStream exportExcel(ExportGoodFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        CoverResponse<List<ExportGoodsDTO>, TotalReport> coverResponse = this.getDataExport(filter);
        ExportGoodsExcel excel = new ExportGoodsExcel(shopDTO, coverResponse.getResponse(),coverResponse.getInfo());
        excel.setFromDate(filter.getFromExportDate());
        excel.setToDate(filter.getToExportDate());
        return excel.export();
    }

    @Override
    public Response<CoverResponse<PrintGoods, TotalReport>> getDataToPrint(ExportGoodFilter filter) {
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("P_PRINT_GOODS",PrintGoodDTO.class)
        .registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR)
        .registerStoredProcedureParameter(2, Date.class, ParameterMode.IN).setParameter(2, filter.getFromExportDate())
        .registerStoredProcedureParameter(3, Date.class, ParameterMode.IN).setParameter(3, filter.getToExportDate())
        .registerStoredProcedureParameter(4, Date.class, ParameterMode.IN).setParameter(4, filter.getFromOrderDate())
        .registerStoredProcedureParameter(5, Date.class, ParameterMode.IN).setParameter(5, filter.getToOrderDate())
        .registerStoredProcedureParameter(6, String.class, ParameterMode.IN).setParameter(6, filter.getLstProduct())
        .registerStoredProcedureParameter(7, String.class, ParameterMode.IN).setParameter(7, filter.getLstExportType())
        .registerStoredProcedureParameter(8, String.class, ParameterMode.IN).setParameter(8, filter.getSearchKeywords())
        .registerStoredProcedureParameter(9, Long.class, ParameterMode.IN).setParameter(9, filter.getShopId())
        .registerStoredProcedureParameter(10, void.class, ParameterMode.REF_CURSOR)
        .registerStoredProcedureParameter(11, void.class, ParameterMode.REF_CURSOR);

        storedProcedure.execute();

        List<PrintGoodDTO> lstAdjust = storedProcedure.getResultList();
        List<PrintGoodDTO> lstPo = new ArrayList<>();
        List<PrintGoodDTO> lstStock = new ArrayList<>();
        if(storedProcedure.hasMoreResults())
            lstPo = storedProcedure.getResultList();
        if(storedProcedure.hasMoreResults())
            lstStock = storedProcedure.getResultList();

        PrintGoods printGoods = new PrintGoods(lstAdjust,lstPo,lstStock);
        TotalReport totalReport = new TotalReport();

        Integer sumQuantity = 0;
        Float sumtotalAmount = 0F;
        if(lstAdjust.size()>0)
        {
            Integer length = lstAdjust.size()-1;
            sumQuantity +=lstAdjust.get(length).getQuantity();
            sumtotalAmount +=lstAdjust.get(length).getTotalAmount();
        }
        if(lstPo.size()>0)
        {
            Integer length = lstPo.size()-1;
            sumQuantity +=lstPo.get(length).getQuantity();
            sumtotalAmount +=lstPo.get(length).getTotalAmount();
        }
        if(lstStock.size()>0)
        {
            Integer length = lstStock.size()-1;
            sumQuantity +=lstStock.get(length).getQuantity();
            sumtotalAmount +=lstStock.get(length).getTotalAmount();
        }

        totalReport.setTotalQuantity(sumQuantity);
        totalReport.setTotalAmount(sumtotalAmount);

        CoverResponse<PrintGoods, TotalReport> coverResponse = new CoverResponse<>(printGoods, totalReport);

        return new Response<CoverResponse<PrintGoods, TotalReport>>().withData(coverResponse);
    }

    public CoverResponse<List<ExportGoodsDTO>, TotalReport> getDataExport(ExportGoodFilter filter)
    {
        StoredProcedureQuery storedProcedure = callPExportGoods(filter);
        List<ExportGoodsDTO> lst = storedProcedure.getResultList();
        TotalReport totalReport = new TotalReport();
        if(lst.size() > 0)
        {
            Integer l = lst.size();
            ExportGoodsDTO lastExportGood = lst.get(l-1);
            totalReport.setTotalAmount(lastExportGood.getTotalAmount());
            totalReport.setTotalQuantity(lastExportGood.getQuantity());
            totalReport.setTotalAmountNotVat(lastExportGood.getAmountNotVat());
            totalReport.setTotalPacketQuantity(lastExportGood.getPacketQuantity());
            totalReport.setTotalUnitQuantity(lastExportGood.getUnitQuantity());
            totalReport.setTotalPrice(lastExportGood.getPrice());
            lst.remove(l-1);
            lst.remove(l-2);
        }

        return new CoverResponse(lst, totalReport);
    }

    public StoredProcedureQuery callPExportGoods(ExportGoodFilter filter)
    {
        StoredProcedureQuery storedProcedure =
                entityManager.createStoredProcedureQuery("P_EXPORT_GOODS", ExportGoodsDTO.class);
        storedProcedure.registerStoredProcedureParameter(1, void.class, ParameterMode.REF_CURSOR);
        storedProcedure.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(3, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(4, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(5, Date.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(7, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(8, String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter(9, Long.class, ParameterMode.IN);

        storedProcedure.setParameter(2, filter.getFromExportDate());
        storedProcedure.setParameter(3, filter.getToExportDate());
        storedProcedure.setParameter(4, filter.getFromOrderDate());
        storedProcedure.setParameter(5, filter.getToOrderDate());
        storedProcedure.setParameter(6, filter.getLstProduct());
        storedProcedure.setParameter(7, filter.getLstExportType());
        storedProcedure.setParameter(8, filter.getSearchKeywords());
        storedProcedure.setParameter(9, filter.getShopId());
        storedProcedure.execute();
        return storedProcedure;
    }
}
