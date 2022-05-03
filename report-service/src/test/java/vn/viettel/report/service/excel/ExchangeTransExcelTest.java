package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class ExchangeTransExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        ExchangeTransFilter changePriceReport = new ExchangeTransFilter();
        ExchangeTransReportDTO tableDynamicDTO = new ExchangeTransReportDTO();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        ExchangeTransExcel excel = new ExchangeTransExcel(changePriceReport,
                shop, tableDynamicDTO, parentShop);
        ByteArrayInputStream result = excel.export();
    }
}