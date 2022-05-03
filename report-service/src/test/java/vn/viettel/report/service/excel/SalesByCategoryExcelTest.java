package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.service.dto.SalesByCategoryReportDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class SalesByCategoryExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        SaleCategoryFilter changePriceReport = new SaleCategoryFilter();
        SalesByCategoryReportDTO dto = new SalesByCategoryReportDTO();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        SalesByCategoryExcel excel = new SalesByCategoryExcel(changePriceReport,
                shop, dto, parentShop);
        ByteArrayInputStream result = excel.export();
    }
}