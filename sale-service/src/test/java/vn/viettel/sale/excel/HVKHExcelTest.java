package vn.viettel.sale.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.service.dto.CTDTO;
import vn.viettel.sale.service.dto.HDDTExcelDTO;
import vn.viettel.sale.service.dto.HDDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HVKHExcelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<HDDTO> lst = new ArrayList<>();
        List<CTDTO> lst1 = new ArrayList<>();
        HDDTO record = new HDDTO();
        lst.add(record);
        CTDTO record1 = new CTDTO();
        lst1.add(record1);
        ShopDTO shop = new ShopDTO();
        HVKHExcel export = new HVKHExcel(lst, lst1);
        export.export();
    }
}