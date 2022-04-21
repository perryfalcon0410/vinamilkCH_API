package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ShopImportExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException, ParseException {
        CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data = new CoverResponse<>();
        data.setInfo(new ShopImportTotalDTO());
        List<ShopImportDTO> lst = new ArrayList<>();
        lst.add(new ShopImportDTO());
        data.setResponse(lst);
        ShopImportFilter filter = new ShopImportFilter();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        ShopImportExcel excel = new ShopImportExcel(data,
                shop, parentShop, filter);
        ByteArrayInputStream result = excel.export();
    }
}