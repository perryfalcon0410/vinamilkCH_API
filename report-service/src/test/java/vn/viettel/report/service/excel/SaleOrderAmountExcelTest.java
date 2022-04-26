package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class SaleOrderAmountExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        SaleOrderAmountFilter changePriceReport = new SaleOrderAmountFilter();
        TableDynamicDTO tableDynamicDTO = new TableDynamicDTO();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        SaleOrderAmountExcel excel = new SaleOrderAmountExcel(changePriceReport,
                tableDynamicDTO, shop, parentShop);
        ByteArrayInputStream result = excel.export();
    }
}