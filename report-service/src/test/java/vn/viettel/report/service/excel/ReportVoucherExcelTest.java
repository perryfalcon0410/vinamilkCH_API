package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.dto.ReportVoucherDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ReportVoucherExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<ReportVoucherDTO> reportVoucherDTOS = new ArrayList<>();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        ReportVoucherExcel excel = new ReportVoucherExcel(
                shop, parentShop, reportVoucherDTOS);
        ByteArrayInputStream result = excel.export();
    }
}