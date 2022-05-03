package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.dto.CustomerReportDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class CustomerNotTradeExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<CustomerReportDTO> customers = new ArrayList<>();
        CustomerReportDTO dto = new CustomerReportDTO();
        customers.add(dto);
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        Date fromDate = null;
        Date toDate = null;
        CustomerNotTradeExcel excel = new CustomerNotTradeExcel(customers,
                shop, parentShop, fromDate, toDate);

        excel.export();
    }
}