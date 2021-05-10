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
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.ExportGoodsDTO;
import vn.viettel.report.service.excel.ExportGoodsExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
        Integer l = lst.size();
        ExportGoodsDTO lastExportGood = lst.get(l-1);
        TotalReport totalReport = new TotalReport();
        totalReport.setTotalAmount(lastExportGood.getTotalAmount());
        totalReport.setTotalQuantity(lastExportGood.getQuantity());
        totalReport.setTotalAmountNotVat(lastExportGood.getAmountNotVat());
        totalReport.setTotalPacketQuantity(lastExportGood.getPacketQuantity());
        totalReport.setTotalUnitQuantity(lastExportGood.getUnitQuantity());

        lst.remove(l-1);
        lst.remove(l-2);
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lst.size());
        Page<ExportGoodsDTO>  page = new PageImpl<>(lst.subList(start, end), pageable, lst.size());

        CoverResponse coverResponse = new CoverResponse(page,totalReport);
        return new Response<CoverResponse<Page<ExportGoodsDTO>, TotalReport>>().withData(coverResponse);
    }

    @Override
    public ByteArrayInputStream exportExcel(ExportGoodFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        CoverResponse<List<ExportGoodsDTO>, TotalReport> coverResponse = this.getDateExport(filter);
        ExportGoodsExcel excel = new ExportGoodsExcel(shopDTO, coverResponse.getResponse(),coverResponse.getInfo());
        excel.setFromDate(filter.getFromExportDate());
        excel.setToDate(filter.getToExportDate());
        return excel.export();
    }

    public CoverResponse<List<ExportGoodsDTO>, TotalReport> getDateExport(ExportGoodFilter filter)
    {
        StoredProcedureQuery storedProcedure = callPExportGoods(filter);
        List<ExportGoodsDTO> lst = storedProcedure.getResultList();
        Integer l = lst.size();
        ExportGoodsDTO lastExportGood = lst.get(l-1);
        TotalReport totalReport = new TotalReport();
        totalReport.setTotalAmount(lastExportGood.getTotalAmount());
        totalReport.setTotalQuantity(lastExportGood.getQuantity());
        totalReport.setTotalAmountNotVat(lastExportGood.getAmountNotVat());
        totalReport.setTotalPacketQuantity(lastExportGood.getPacketQuantity());
        totalReport.setTotalUnitQuantity(lastExportGood.getUnitQuantity());
        lst.remove(l-1);
        lst.remove(l-2);

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
