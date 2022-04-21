package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.dto.ShopExportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShopExportExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        ChangePriceReportRequest changePriceReport = new ChangePriceReportRequest();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        List<ShopExportDTO> exportGoodsDTOS = new ArrayList<>();
        TotalReport totalReport = new TotalReport();
        ShopExportExcel excel = new ShopExportExcel(
                shop, parentShop, exportGoodsDTOS, totalReport);
        ByteArrayInputStream result = excel.export();
    }
}