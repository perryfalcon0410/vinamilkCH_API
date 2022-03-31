package vn.viettel.sale.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.service.dto.CTDTO;
import vn.viettel.sale.service.dto.HDDTO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SampleExcelTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        SampleExcel export = new SampleExcel(LocalDateTime.now());
        export.export();
    }
}