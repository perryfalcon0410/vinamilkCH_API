package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class InOutAdjustmentExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<InOutAdjusmentDTO> data = new ArrayList<>();
        InOutAdjustmentFilter filter = new InOutAdjustmentFilter();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        InOutAdjustmentExcel excel = new InOutAdjustmentExcel(data,
                shop, parentShop, filter);
        ByteArrayInputStream result = excel.export();
    }
}