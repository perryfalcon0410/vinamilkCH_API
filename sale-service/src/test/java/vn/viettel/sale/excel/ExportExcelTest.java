package vn.viettel.sale.excel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.dto.PoDetailDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExportExcelTest extends BaseTest {

    @Mock
    ExportExcel exportExcel;

    @Before
    public void init() {

    }

    @Test
    public void export() throws IOException {
        List<PoDetailDTO> poDetails = new ArrayList<>();
        PoDetailDTO poDetailDTO = new PoDetailDTO();
        List<PoDetailDTO> poDetails2 = new ArrayList<>();
        poDetails.add(poDetailDTO);
        poDetails2.add(poDetailDTO);
        ShopDTO shop = new ShopDTO();
        ExportExcel exportExcel = new ExportExcel(poDetails, poDetails2, shop);
        exportExcel.export();
    }
}