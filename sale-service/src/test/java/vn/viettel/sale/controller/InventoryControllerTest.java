package vn.viettel.sale.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class InventoryControllerTest extends BaseTest {
    private final String root = "/sales";
    @MockBean
    private InventoryService inventoryService;

    @Test
    public void testIndex() throws Exception {
        String uri = V1 + root + "/inventory";
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<StockCountingDTO> list = Arrays.asList(new StockCountingDTO(), new StockCountingDTO());
        Page<StockCountingDTO> stockCountingDTOS = new PageImpl<>(list, pageRequest , list.size());
        Response<Page<StockCountingDTO>> data = new Response<>();
        data.setData(stockCountingDTOS);
        given(inventoryService.index(any(), any(), any(), any(), Mockito.any(PageRequest.class))).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

//    @Test
    public void testGetAll() throws Exception {
        String uri = V1 + root + "/inventories";
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<StockCountingDTO> list = Arrays.asList(new StockCountingDTO(), new StockCountingDTO());
        Page<StockCountingDTO> stockCountingDTOS = new PageImpl<>(list, pageRequest, list.size());
        CoverResponse<Page<StockCountingDTO>, TotalStockCounting> data =
                new CoverResponse<>(stockCountingDTOS, new TotalStockCounting());
        Response<CoverResponse<Page<StockCountingDTO>,TotalStockCounting>> response = new Response<>();
        response.setData(data);
        Object obj = new Object();
        given(inventoryService.getAll(Mockito.any(PageRequest.class),
                true)).willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void testGetStockCountingDetails() throws Exception {
        String uri = V1 + root + "/inventory/{id}";
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<StockCountingExcel> list = Arrays.asList(new StockCountingExcel(), new StockCountingExcel());
        Page<StockCountingExcel> stockCountingDTOS = new PageImpl<>(list, pageRequest, list.size());
        CoverResponse<Page<StockCountingExcel>, TotalStockCounting> data =
                new CoverResponse<>(stockCountingDTOS, new TotalStockCounting());
        Response<CoverResponse<Page<StockCountingExcel>,TotalStockCounting>> response = new Response<>();
        response.setData(data);
        given(inventoryService.getByStockCountingId(any(),Mockito.any(PageRequest.class))).willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

//    @Test
    public void testImportExcel() throws Exception {
        String uri = V1 + root + "/inventory/import-excel";
        StockCountingImportDTO stockCountingImportDTO = new StockCountingImportDTO();
        List<StockCountingDetailDTO> importSuccess = Arrays.asList(new StockCountingDetailDTO(), new StockCountingDetailDTO());
        List<StockCountingExcel> importFails = Arrays.asList(new StockCountingExcel(), new StockCountingExcel());
        stockCountingImportDTO.setImportSuccess(importSuccess);
        stockCountingImportDTO.setImportFails(importFails);
        CoverResponse<StockCountingImportDTO,InventoryImportInfo> data =
                new CoverResponse<>(stockCountingImportDTO, new InventoryImportInfo());
        given(inventoryService.importExcel(any(),Mockito.any(PageRequest.class))).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    public void testUpdateStockCounting() {
    }

    public void testStockCountingExport() {
    }

    public void testStockCountingExportFail() {
    }

    public void testStockCountingExportAll() {
    }

    public void testCreateStockCounting() {
    }

    public void testGetInventoryNumberInDay() {
    }
}