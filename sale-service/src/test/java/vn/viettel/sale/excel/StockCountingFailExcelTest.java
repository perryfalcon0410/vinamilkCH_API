package vn.viettel.sale.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.service.dto.CTDTO;
import vn.viettel.sale.service.dto.HDDTO;
import vn.viettel.sale.service.dto.StockCountingExcel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StockCountingFailExcelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<StockCountingExcel> lst = new ArrayList<>();
        List<CTDTO> lst1 = new ArrayList<>();
        StockCountingExcel record = new StockCountingExcel();
        lst.add(record);
        CTDTO record1 = new CTDTO();
        lst1.add(record1);
        ShopDTO shop = new ShopDTO();
        StockCountingFailExcel export = new StockCountingFailExcel(lst, LocalDateTime.now());
        export.export();
    }
}