package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.sale.entities.PoTransDetail;
import vn.viettel.sale.messaging.ReceiptCreateDetailRequest;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.repository.PoTransDetailRepository;
import vn.viettel.sale.repository.PoTransRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.PoTransDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;
import vn.viettel.sale.service.impl.ReceiptExportServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReceiptExportControllerTest extends BaseTest {
    private final String root = "/sales/export";

    @InjectMocks
    ReceiptExportServiceImpl serviceImp;

    @Mock
    ReceiptExportService service;

    @Mock
    PoTransRepository repository;

    @Mock
    PoTransDetailRepository poTransDetailRepository;

    @Mock
    StockTotalRepository stockTotalRepository;

    @Mock
    StockTotalService stockTotalService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ReceiptExportController controller = new ReceiptExportController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void find() throws Exception {
        String uri = V1 + root;
        Long shopId = 1L;
        String transCode = "";
        String redInvoiceNo = "";
        LocalDateTime fromDate = null;
        LocalDateTime toDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(1, 20);
        List<ReceiptImportListDTO> list = Arrays.asList(new ReceiptImportListDTO(), new ReceiptImportListDTO());
        Page<ReceiptImportListDTO> page = new PageImpl<>(list);
        when(repository.getReceiptPo(any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);
        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> result = serviceImp.find(transCode, redInvoiceNo, fromDate,
                toDate, 0, shopId, pageable);
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void create() throws Exception {
        String uri = V1 + root;
        Long id = 1L;
        ReceiptExportCreateRequest request = new ReceiptExportCreateRequest();
        request.setImportType(0);
        request.setReceiptImportId(id);
        request.setInternalNumber("PO12345");
        request.setRedInvoiceNo("RE123");
        request.setIsRemainAll(true);
        request.setNote("Ghi chu");
        request.setTransCode("CD001");
        request.setLitQuantityRemain(Arrays.asList(new ReceiptCreateDetailRequest(), new ReceiptCreateDetailRequest()));
        ResponseMessage response = ResponseMessage.SUCCESSFUL;
        PoTrans poTrans = new PoTrans();
        poTrans.setWareHouseTypeId(2L);
        poTrans.setId(id);
        List<PoTransDetail> poTransDetails = new ArrayList<>();
//        when(repository.findByIdAndShopIdAndTypeAndStatus(request.getReceiptImportId(), 1L, 1,1 )).thenReturn(java.util.Optional.of(poTrans));
//        when(poTransDetailRepository.getPoTransDetailByTransId(id)).thenReturn(poTransDetails);
        ResponseMessage responseMessage = service.createReceipt( request, 1L, 1L);
        System.out.println("jjjjjjjjj " + responseMessage);
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
        service.updateReceiptExport(any(), any(),any());
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
        service.removeReceiptExport(any(), any(),any());
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
        service.getListPoTrans(any(), any(), any(), any(), any(),any(), any(), Mockito.any(PageRequest.class));

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void getListStockBorrowing() throws Exception {
        String uri = V1 + root + "/borrowing";
        List<StockBorrowingDTO> list = Arrays.asList(new StockBorrowingDTO(), new StockBorrowingDTO());
        service.getListStockBorrowing(any(), Mockito.any(PageRequest.class));

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
