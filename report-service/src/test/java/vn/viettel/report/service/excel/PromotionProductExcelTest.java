package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.dto.PromotionProductDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PromotionProductExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException {
        PromotionProductDTO total = new PromotionProductDTO();
        PromotionProductFilter filter = new PromotionProductFilter();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        List<PromotionProductDTO> promotionDetails = Arrays.asList(new PromotionProductDTO());
        PromotionProductExcel excel = new PromotionProductExcel(
                shop, parentShop, total, filter);
        excel.setPromotionDetails(promotionDetails);
        excel.setPromotionIndays(promotionDetails);
        excel.setPromotionproducts(promotionDetails);
        ByteArrayInputStream result = excel.export();
    }
}