package vn.viettel.sale.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.CommonService;
import vn.viettel.sale.service.dto.ImportTypeDTO;
import vn.viettel.sale.service.dto.PoConfirmStatusDTO;
import vn.viettel.sale.service.dto.StockAdjustmentStatusDTO;
import vn.viettel.sale.service.dto.StockBorrowingStatusDTO;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CommonControllerTest extends BaseTest {
    private final String root = "/sales/commons";
    private final String uri = V1 + root;

    @MockBean
    CommonService service;

    //-------------------------------getImportType-------------------------------
    @Test
    public void getImportTypeTest() throws Exception {
        String url = uri + "/import-type";

        List<ImportTypeDTO> result = new ArrayList<>();

        given(service.getList()).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("["));
    }

    //-------------------------------getPoConfirmStatus-------------------------------
    @Test
    public void getPoConfirmStatusTest() throws Exception {
        String url = uri + "/po-confirm-status";

        List<PoConfirmStatusDTO> result = new ArrayList<>();

        given(service.getListPoConfirmStatusDTO()).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("["));
    }

    //-------------------------------getStockAdjustmentStatus-------------------------------
    @Test
    public void getStockAdjustmentStatusTest() throws Exception {
        String url = uri + "/adjustment-status";

        List<StockAdjustmentStatusDTO> result = new ArrayList<>();

        given(service.getListStockAdjustmentTypeDTO()).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("["));
    }

    //-------------------------------getStockBorrowingStatus-------------------------------
    @Test
    public void getStockBorrowingStatusTest() throws Exception {
        String url = uri + "/borrowing-status";

        List<StockBorrowingStatusDTO> result = new ArrayList<>();

        given(service.getListStockBorrowingTypeDTO()).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("["));
    }
}
