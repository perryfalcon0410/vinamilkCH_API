package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.dto.PrintInventoryDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class ImportExportInventoryExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        PrintInventoryDTO inventoryDTO = new PrintInventoryDTO();
        inventoryDTO.setShop(new ShopDTO());
        InventoryImportExportFilter filter = new InventoryImportExportFilter();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        ImportExportInventoryExcel excel = new ImportExportInventoryExcel(
                shop, inventoryDTO, filter);
        ByteArrayInputStream result = excel.export();
    }
}