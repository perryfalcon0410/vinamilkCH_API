package vn.viettel.sale.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.service.dto.HDDTExcelDTO;
import vn.viettel.sale.service.dto.PoDetailDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HDDTExcelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        List<HDDTExcelDTO> lst = new ArrayList<>();
        HDDTExcelDTO record = new HDDTExcelDTO();
        record.setPaymentType(1);
        lst.add(record);
        ShopDTO shop = new ShopDTO();
        HDDTExcel export = new HDDTExcel(lst);
        export.export();
    }
}