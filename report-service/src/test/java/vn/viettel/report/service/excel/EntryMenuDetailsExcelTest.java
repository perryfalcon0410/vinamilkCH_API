package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.EntryMenuDetailsReportsRequest;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EntryMenuDetailsExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<vn.viettel.report.service.dto.EntryMenuDetailsDTO> entryMenuDetailsDTOS = new ArrayList<>();
        EntryMenuDetailsDTO total = new EntryMenuDetailsDTO();
        EntryMenuDetailsReportsRequest filter = new EntryMenuDetailsReportsRequest();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        EntryMenuDetailsExcel excel = new EntryMenuDetailsExcel(
                shop, parentShop, entryMenuDetailsDTOS, total, filter);
        ByteArrayInputStream result = excel.export();
    }
}