package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ChangePriceReportExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        ChangePriceReportRequest changePriceReport = new ChangePriceReportRequest();
        changePriceReport.setReportTotal(new ChangePriceTotalDTO());
        List<ChangePriceDTO> lst = new ArrayList<>();
        ChangePriceDTO dto = new ChangePriceDTO();
        lst.add(dto);
        dto.setOrderDate(LocalDateTime.now());
        dto.setPoNumber("123");
        changePriceReport.setChangePriceReport(lst);
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        ChangePriceReportExcel excel = new ChangePriceReportExcel(changePriceReport,
                shop, parentShop, fromDate, toDate);
        ByteArrayInputStream result = excel.export();
    }
}