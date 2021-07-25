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
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReceiptImportControllerTest extends BaseTest {
    private final String root = "/sales/import";

    @MockBean
    ReceiptImportService receiptService;

    @Test
    public void findALlProductInfo() throws Exception {
        String uri = V1 + root ;
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ReceiptImportListDTO> list = Arrays.asList(new ReceiptImportListDTO(), new ReceiptImportListDTO());
        Page<ReceiptImportListDTO> redInvoiceDTOs = new PageImpl<>(list, pageRequest , list.size());
        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> data =
                new CoverResponse<>(redInvoiceDTOs, new TotalResponse());

        given(receiptService.find(any(),any(), any(), any(),  any(),  any(), Mockito.any(PageRequest.class))).willReturn(data);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void create() throws Exception {
        String uri = V1 + root;
        ReceiptCreateRequest request = new ReceiptCreateRequest();
        request.setPoId(1L);
        request.setImportType(1);
        request.setInternalNumber("PO12345");
        request.setPoCoNumber("CO123");
        request.setRedInvoiceNo("RE123");
        request.setLst(Arrays.asList(new ReceiptCreateDetailRequest(), new ReceiptCreateDetailRequest()));
        ResponseMessage response = ResponseMessage.SUCCESSFUL;
        List<Long> ids = new ArrayList<Long>();
        ids.add(1l);
        given(receiptService.createReceipt(any(), any(), any())).willReturn(ids);
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
    public void getListPoConfirm() throws Exception {
        String uri = V1 + root + "/po-confirm";
        List<PoConfirmDTO> response = Arrays.asList(new PoConfirmDTO(), new PoConfirmDTO());
        given(receiptService.getListPoConfirm(any())).willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }

    @Test
    public void getListStockAdjustment() throws Exception {
        String uri = V1 + root + "/adjustment" ;
        List<StockAdjustmentDTO> list = Arrays.asList(new StockAdjustmentDTO(), new StockAdjustmentDTO());
        given(receiptService.getListStockAdjustment(any(), Mockito.any(PageRequest.class))).willReturn(list);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }

    @Test
    public void getListStockBorrowing() throws Exception {
        String uri = V1 + root + "/borrowing" ;

        List<StockBorrowingDTO> list = Arrays.asList(new StockBorrowingDTO(), new StockBorrowingDTO());
        given(receiptService.getListStockBorrowing(any(), Mockito.any(PageRequest.class))).willReturn(list);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }

    @Test
    public void orderSelected() throws Exception {
        String uri = V1 + root + "/po-detail0/{id}";
        List<PoDetailDTO> list = Arrays.asList(new PoDetailDTO(), new PoDetailDTO());
        CoverResponse<List<PoDetailDTO>, TotalResponseV1> response  =
                new CoverResponse<>(list, new TotalResponseV1());
        given(receiptService.getPoDetailByPoId(any(), any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void getPoDetailByPoIdAndPriceIsNull() throws Exception {
        String uri = V1 + root + "/po-detail1/{id}";
        List<PoDetailDTO> list = Arrays.asList(new PoDetailDTO(), new PoDetailDTO());
        CoverResponse<List<PoDetailDTO>,TotalResponseV1> response =
                new CoverResponse<>(list, new TotalResponseV1());
        given(receiptService.getPoDetailByPoIdAndPriceIsNull(any(), any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void getStockAdjustmentDetail() throws Exception {
        String uri = V1 + root + "/adjustment-detail/{id}";
        List<StockAdjustmentDetailDTO> list = Arrays.asList(new StockAdjustmentDetailDTO(), new StockAdjustmentDetailDTO());
        CoverResponse<List<StockAdjustmentDetailDTO>,TotalResponse> response =
                new CoverResponse<>(list, new TotalResponse());
        given(receiptService.getStockAdjustmentDetail(any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void getStockBorrowingDetail() throws Exception {
        String uri = V1 + root + "/borrowing-detail/{id}";
        List<StockBorrowingDetailDTO> list = Arrays.asList(new StockBorrowingDetailDTO(), new StockBorrowingDetailDTO());
        CoverResponse<List<StockBorrowingDetailDTO>,TotalResponse> response =
                new CoverResponse<>(list, new TotalResponse());
        given(receiptService.getStockBorrowingDetail(any())).willReturn(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void getWareHouseType() throws Exception {
        String uri = V1 + root + "/warehouse-type";
        WareHouseTypeDTO wareHouseTypeDTO = new WareHouseTypeDTO();

        given(receiptService.getWareHouseTypeName(any())).willReturn(wareHouseTypeDTO);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void setNotImport() throws Exception {
        String uri = V1 + root + "/not-import/{Id}";
        NotImportRequest request = new NotImportRequest();
        request.setId(1L);
        request.setReasonDeny(1);
        ResponseMessage response = ResponseMessage.SUCCESSFUL;

        given(receiptService.setNotImport(any(), any(),any())).willReturn(response);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri, 1L)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
