package vn.viettel.sale.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.service.dto.CTDTO;
import vn.viettel.sale.service.dto.HDDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StockCountingAllExcelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<StockCountingDetailDTO> lst = new ArrayList<>();
        List<CTDTO> lst1 = new ArrayList<>();
        StockCountingDetailDTO record = new StockCountingDetailDTO();
        record.setStockQuantity(1);
        record.setTotalAmount(50.2);
        record.setChangeQuantity(3);
        record.setUnitQuantity(1);
        record.setInventoryQuantity(2);
        lst.add(record);
        CTDTO record1 = new CTDTO();
        lst1.add(record1);
        ShopDTO shop = new ShopDTO();
        StockCountingAllExcel export = new StockCountingAllExcel(lst, shop, shop, LocalDateTime.now());
        export.export();
    }
}