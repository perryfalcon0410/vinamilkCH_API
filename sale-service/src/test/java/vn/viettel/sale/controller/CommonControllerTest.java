package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.CommonService;
import vn.viettel.sale.service.dto.ImportTypeDTO;
import vn.viettel.sale.service.dto.PoConfirmStatusDTO;
import vn.viettel.sale.service.dto.StockAdjustmentStatusDTO;
import vn.viettel.sale.service.dto.StockBorrowingStatusDTO;
import vn.viettel.sale.service.impl.CommonServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CommonControllerTest extends BaseTest {
    private final String root = "/sales/commons";
    private final String uri = V1 + root;

    @InjectMocks
    CommonServiceImpl serviceImp;

    @Mock
    CommonService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final CommonController controller = new CommonController();
        controller.setService(service);
        this.setupAction(controller);
    }

    //-------------------------------getImportType-------------------------------
    @Test
    public void getImportTypeTest() throws Exception {
        String url = uri + "/import-type";

        List<ImportTypeDTO> result = serviceImp.getList();

        ResultActions resultActions = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getPoConfirmStatus-------------------------------
    @Test
    public void getPoConfirmStatusTest() throws Exception {
        String url = uri + "/po-confirm-status";

        List<PoConfirmStatusDTO> result = serviceImp.getListPoConfirmStatusDTO();
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------getStockAdjustmentStatus-------------------------------
    @Test
    public void getStockAdjustmentStatusTest() throws Exception {
        String url = uri + "/adjustment-status";

        List<StockAdjustmentStatusDTO> result = serviceImp.getListStockAdjustmentTypeDTO();

        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------getStockBorrowingStatus-------------------------------
    @Test
    public void getStockBorrowingStatusTest() throws Exception {
        String url = uri + "/borrowing-status";

        List<StockBorrowingStatusDTO> result = serviceImp.getListStockBorrowingTypeDTO();
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
