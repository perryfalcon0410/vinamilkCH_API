package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.dto.StockTotalExcelRequest;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StockTotalReportExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        StockTotalExcelRequest stockTotalExcelRequest = new StockTotalExcelRequest();
        List<StockTotalReportDTO> stockTotals = Arrays.asList(new StockTotalReportDTO());
        stockTotalExcelRequest.setStockTotals(stockTotals);
        stockTotalExcelRequest.setTotalInfo(new StockTotalInfoDTO());
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        StockTotalReportExcel excel = new StockTotalReportExcel(stockTotalExcelRequest,
                shop, parentShop, fromDate);
        ByteArrayInputStream result = excel.export();
    }
}