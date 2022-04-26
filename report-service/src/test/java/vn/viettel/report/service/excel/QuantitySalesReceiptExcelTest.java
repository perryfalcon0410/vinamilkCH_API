package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class QuantitySalesReceiptExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        QuantitySalesReceiptFilter filter = new QuantitySalesReceiptFilter();
        vn.viettel.report.service.dto.TableDynamicDTO tableDynamicDTO = new TableDynamicDTO();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        QuantitySalesReceiptExcel excel = new QuantitySalesReceiptExcel(filter,
                tableDynamicDTO, shop, parentShop);
        ByteArrayInputStream result = excel.export();
    }
}