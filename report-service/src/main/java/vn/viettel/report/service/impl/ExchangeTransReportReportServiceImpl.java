package vn.viettel.report.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.ExchangeTransReportService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.excel.ExchangeTransExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class ExchangeTransReportReportServiceImpl implements ExchangeTransReportService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ShopClient shopClient;

    @Override
    public ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException {
        ShopDTO shopDTO = shopClient.getShopByIdV1(filter.getShopId()).getData();
        List<ExchangeTransReportDTO> exchangeTransList = this.callStoreProcedure();
        ExchangeTransReportDTO exchangeTransTotal = new ExchangeTransReportDTO();
        if(!exchangeTransList.isEmpty()) {
            exchangeTransTotal = exchangeTransList.get(exchangeTransList.size() -1);
            this.removeDataList(exchangeTransList);
        }
        ExchangeTransExcel excel = new ExchangeTransExcel(shopDTO, exchangeTransList, exchangeTransTotal);
        excel.setFromDate(filter.getFromDate());
        excel.setToDate(filter.getToDate());
        return excel.export();
    }

    private List<ExchangeTransReportDTO> callStoreProcedure() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("P_EXCHANGE_TRANS", ExchangeTransReportDTO.class);
        query.registerStoredProcedureParameter("promotionDetails", void.class,  ParameterMode.REF_CURSOR);
        query.execute();
        List<ExchangeTransReportDTO> reportDTOS = query.getResultList();
        return reportDTOS;
    }

    private void removeDataList(List<ExchangeTransReportDTO> exchangeTrans) {
        exchangeTrans.remove(exchangeTrans.size()-1);
        exchangeTrans.remove(exchangeTrans.size()-1);
    }
}
