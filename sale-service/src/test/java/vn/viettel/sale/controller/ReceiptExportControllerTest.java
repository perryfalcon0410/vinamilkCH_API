package vn.viettel.sale.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.messaging.ReceiptCreateDetailRequest;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.PoTransDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReceiptExportControllerTest extends BaseTest{
    private final String root = "/sales/export";

    @MockBean
    ReceiptExportService receiptExportService;

    @Test
    public void find() throws Exception {
        String uri = V1 + root;
        List<ReceiptImportListDTO> list = Arrays.asList(new ReceiptImportListDTO(), new ReceiptImportListDTO());
        Page<ReceiptImportListDTO> page = new PageImpl<>(list);
        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response =
                new CoverResponse<>(page, new TotalResponse());
        given(receiptExportService.find(any(), any(), any(), any(), any(), Mockito.any(PageRequest.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void create() throws Exception {
        String uri = V1 + root;
        ReceiptExportCreateRequest request = new ReceiptExportCreateRequest();
        request.setImportType(1);
        request.setInternalNumber("PO12345");
        request.setRedInvoiceNo("RE123");
        request.setIsRemainAll(true);
        request.setNote("Ghi chu");
        request.setTransCode("CD001");
        request.setLitQuantityRemain(Arrays.asList(new ReceiptCreateDetailRequest(), new ReceiptCreateDetailRequest()));
        ResponseMessage response = ResponseMessage.SUCCESSFUL;

        given(receiptExportService.createReceipt(any(), any(), any())).willReturn(response);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void updateReceiptExport() throws Exception {
        String uri = V1 + root + "/update/{Id}";
        ReceiptExportUpdateRequest request = new ReceiptExportUpdateRequest();
        request.setId(1L);
        request.setType(1);
        request.setListProductRemain(Arrays.asList(new ReceiptCreateDetailRequest(), new ReceiptCreateDetailRequest()));
        request.setNote("Ghi chu 2");
        ResponseMessage response = ResponseMessage.SUCCESSFUL;
        given(receiptExportService.updateReceiptExport(any(), any())).willReturn(response);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.patch(uri, 1L)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void removeReceiptExport() throws Exception {
        String uri = V1 + root + "/remove/1?type=2";
        ResponseMessage response = ResponseMessage.SUCCESSFUL;
        given(receiptExportService.removeReceiptExport(any(), any())).willReturn(response);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getListPoTrans() throws Exception {
        String uri = V1 + root + "/po-trans";
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<PoTransDTO> list = Arrays.asList(new PoTransDTO(), new PoTransDTO());
        Page<PoTransDTO> pageResponse =  new PageImpl<>(list, pageRequest , list.size());
        given(receiptExportService.getListPoTrans(any(), any(), any(), any(), any(), any(), Mockito.any(PageRequest.class))).willReturn(pageResponse);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void getListStockBorrowing() throws Exception {
        String uri = V1 + root + "/borrowing";
        List<StockBorrowingDTO> list = Arrays.asList(new StockBorrowingDTO(), new StockBorrowingDTO());
        given(receiptExportService.getListStockBorrowing(any(), Mockito.any(PageRequest.class))).willReturn(list);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }

}
