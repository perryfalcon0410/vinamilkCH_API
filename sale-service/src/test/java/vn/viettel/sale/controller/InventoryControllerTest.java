package vn.viettel.sale.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.*;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class InventoryControllerTest extends BaseTest {
    private final String root = "/sales";
    @MockBean
    private InventoryService inventoryService;
    @Autowired
    private WebApplicationContext webApplicationContext;

   /* @Test
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
    }*/

    @Test
    public void testGetAll() throws Exception {
        String uri = V1 + root + "/inventories?isPaging=true";
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<StockCountingDTO> list = Arrays.asList(new StockCountingDTO(), new StockCountingDTO());
        Page<StockCountingDTO> stockCountingDTOS = new PageImpl<>(list, pageRequest, list.size());
        CoverResponse<Page<StockCountingDTO>, TotalStockCounting> data =
                new CoverResponse<>(stockCountingDTOS, new TotalStockCounting());
        Object obj = new Object();
        given(inventoryService.getAll(  any(), any())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    /*@Test
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
    }*/

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
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        given(inventoryService.importExcel(any(), firstFile,Mockito.any(PageRequest.class), any())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void testUpdateStockCounting() throws Exception {
        String uri = V1 + root + "/inventory/{id}";
        List<StockCountingDetail> list = Arrays.asList(new StockCountingDetail(), new StockCountingDetail());
        List<StockCountingUpdateDTO> request = Arrays.asList(new StockCountingUpdateDTO(), new StockCountingUpdateDTO());
        given(inventoryService.updateStockCounting(any(), any(), any())).willReturn(list);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions = mockMvc.perform(put(uri, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }

    public void testStockCountingExport() {
    }

    public void testStockCountingExportFail() {
    }

    public void testStockCountingExportAll() {
    }

    @Test
    public void testCreateStockCounting() throws Exception {
        String uri = V1 + root + "/inventory?override=true";
        List<StockCountingDetailDTO> request = Arrays.asList(new StockCountingDetailDTO(), new StockCountingDetailDTO());
        StockCounting stockCounting = new StockCounting();
        given(inventoryService.createStockCounting(any(), any(), any(), any())).willReturn(stockCounting);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("{"));
    }

    @Test
    public void testGetInventoryNumberInDay() throws Exception {
        String uri = V1 + root + "/inventory/numInDay";
        given(inventoryService.checkInventoryInDay(any())).willReturn(true);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();

        assertThat(responseData, containsString("\"data\":true"));
    }
}