package vn.viettel.report.service.excel;

import org.junit.Before;
import org.junit.Test;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.messaging.ChangeReturnGoodsReportRequest;
import vn.viettel.report.messaging.ReturnGoodsReportsRequest;
import vn.viettel.report.service.dto.ReturnGoodsDTO;
import vn.viettel.report.service.dto.ReturnGoodsReportTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ReturnGoodsExcelTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void export() throws IOException, ParseException {
        ChangeReturnGoodsReportRequest reportRequest = new ChangeReturnGoodsReportRequest();
        List<ReturnGoodsDTO> lst = new ArrayList<>();
        ReturnGoodsDTO returnGoodsDTO = new ReturnGoodsDTO();
        returnGoodsDTO.setReturnCode("123");
        returnGoodsDTO.setQuantity(5);
        returnGoodsDTO.setAmount(5.0);
        returnGoodsDTO.setRefunds(1.0);
        lst.add(returnGoodsDTO);
        reportRequest.setReturnGoodsDTOS(lst);
        reportRequest.setTotalDTO(new ReturnGoodsReportTotalDTO());
        ReturnGoodsReportsRequest filter = new ReturnGoodsReportsRequest();
        ShopDTO shop = new ShopDTO();
        vn.viettel.core.dto.ShopDTO parentShop = new ShopDTO();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        ReturnGoodsExcel excel = new ReturnGoodsExcel(
                shop, parentShop, reportRequest, filter);
        ByteArrayInputStream result = excel.export();
    }
}