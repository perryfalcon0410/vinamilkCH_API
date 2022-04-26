package vn.viettel.sale.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.service.dto.CTDTO;
import vn.viettel.sale.service.dto.HDDTO;
import vn.viettel.sale.service.dto.StockCountingExcelDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StockCountingFilledExcelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<StockCountingExcelDTO> lst = new ArrayList<>();
        List<CTDTO> lst1 = new ArrayList<>();
        StockCountingExcelDTO record = new StockCountingExcelDTO();
        record.setInventoryQuantity(1);
        record.setUnitQuantity(1);
        record.setChangeQuantity(1);
        record.setTotalAmount(4.0);
        record.setStockQuantity(1);
        lst.add(record);
        CTDTO record1 = new CTDTO();
        lst1.add(record1);
        ShopDTO shop = new ShopDTO();
        StockCountingFilledExcel export = new StockCountingFilledExcel(lst, shop, shop, LocalDateTime.now());
        export.export();
    }
}