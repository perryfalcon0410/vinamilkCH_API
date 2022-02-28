package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.repository.StockCountingDetailRepository;
import vn.viettel.sale.repository.StockCountingRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.impl.InventoryServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InventoryControllerTest extends BaseTest {
    private final String root = "/sales";

    @InjectMocks
    InventoryServiceImpl serviceImp;

    @Mock
    InventoryService service;

    @Mock
    StockCountingRepository repository;

    @Mock
    StockTotalRepository stockTotalRepository;

    @Mock
    StockCountingDetailRepository countingDetailRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final InventoryController controller = new InventoryController();
        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void testIndex() throws Exception {
        String uri = V1 + root + "/inventory";
        Long wareHouseTypeId = 1L;
        service.index("code", wareHouseTypeId, LocalDateTime.now(), LocalDateTime.now(), 1L, PageRequest.of(2, 5));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("stockCountingCode", "stockCountingCode");
        params.add("wareHouseTypeId", wareHouseTypeId.toString());
        LocalDate localDate = LocalDate.now();//For reference
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        params.add("fromDate" , localDate.format(formatter));
        params.add("toDate" , localDate.format(formatter));
        ResultActions resultActions = mockMvc.perform(get(uri+ "?page=1&size=10")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

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
        given(service.getAll( any(), any(), any())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testGetStockCountingDetails() throws Exception {
        String uri = V1 + root + "/inventory/{id}";

        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<StockCountingExcelDTO> list = Arrays.asList(new StockCountingExcelDTO(), new StockCountingExcelDTO());
        CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response =
                new CoverResponse<>(list, new TotalStockCounting());
        given(service.getByStockCountingId(any(),any())).willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testImportExcel() throws Exception {
        String uri = V1 + root + "/inventory/import-excel";
        Long wareHouseTypeId = 1L;
        StockCountingImportDTO stockCountingImportDTO = new StockCountingImportDTO();
        List<StockCountingDetailDTO> importSuccess = Arrays.asList(new StockCountingDetailDTO(), new StockCountingDetailDTO());
        List<StockCountingExcel> importFails = Arrays.asList(new StockCountingExcel(), new StockCountingExcel());
        stockCountingImportDTO.setImportSuccess(importSuccess);
        stockCountingImportDTO.setImportFails(importFails);
        CoverResponse<StockCountingImportDTO,InventoryImportInfo> data =
                new CoverResponse<>(stockCountingImportDTO, new InventoryImportInfo());
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
//        given(service.importExcel(1L, firstFile, PageRequest.of(1, 20), "key",1L)).willReturn(data);
        service.importExcel(1L, firstFile, PageRequest.of(1, 20), "key",wareHouseTypeId);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("file", "" + firstFile);
        params.add("searchKeywords", "searchKeywords");
//        params.add("wareHouseTypeId", "1");

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("wareHouseTypeId", "1L")
//                .params(params)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testUpdateStockCounting() throws Exception {
        String uri = V1 + root + "/inventory/{id}";
        Long shopId = 1L;
        Long stockCountingId = 1L;
        Long wareHouseTypeId = 1L;
        StockCounting stockCounting = new StockCounting();
        stockCounting.setId(1L);
        stockCounting.setCountingDate(LocalDateTime.now());
        stockCounting.setWareHouseTypeId(wareHouseTypeId);
        when(repository.getByIdAndShopId(1L, shopId)).thenReturn(stockCounting);

        List<StockCountingDetail> stockCountingDetails = new ArrayList<>();
        List<StockTotal> stockTotals = new ArrayList<>();
        List<StockCountingUpdateDTO> request = new ArrayList<>();
        for(int i = 1; i < 6; i++){
            StockCountingDetail dtl = new StockCountingDetail();
            dtl.setId((long)i);
            dtl.setProductId((long)i);
            dtl.setStockCountingId(stockCountingId);
            stockCountingDetails.add(dtl);

            StockTotal st = new StockTotal();
            st.setId((long)i);
            st.setShopId(shopId);
            st.setWareHouseTypeId(wareHouseTypeId);
            st.setProductId((long)i);
            st.setQuantity(20);
            st.setStatus(1);
            stockTotals.add(st);

            StockCountingUpdateDTO dtoDtl = new StockCountingUpdateDTO();
            dtoDtl.setProductId((long)i);
            request.add(dtoDtl);
        }
        when(countingDetailRepository.findByStockCountingId(stockCountingId)).thenReturn(stockCountingDetails);
        given(stockTotalRepository.getStockTotal(shopId, wareHouseTypeId)).willReturn(stockTotals);
        ResponseMessage result  = serviceImp.updateStockCounting(stockCountingId, shopId, "admin",request);
        assertNotNull(result);
        assertEquals(result, ResponseMessage.UPDATE_SUCCESSFUL);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions = mockMvc.perform(put(uri, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test(expected=ValidateException.class)
    public void testUpdateStockCountingNOTFOUND() throws Exception {
        String uri = V1 + root + "/inventory/{id}";
        Long shopId = 1L;
        Long stockCountingId = 1L;
        Long wareHouseTypeId = 1L;
        StockCounting stockCounting = new StockCounting();
        stockCounting.setId(1L);
        stockCounting.setCountingDate(LocalDateTime.now());
        stockCounting.setWareHouseTypeId(wareHouseTypeId);
        when(repository.getByIdAndShopId(1L, shopId)).thenReturn(null);

        List<StockCountingDetail> stockCountingDetails = new ArrayList<>();
        List<StockTotal> stockTotals = new ArrayList<>();
        List<StockCountingUpdateDTO> request = new ArrayList<>();
        for(int i = 1; i < 6; i++){
            StockCountingDetail dtl = new StockCountingDetail();
            dtl.setId((long)i);
            dtl.setProductId((long)i);
            dtl.setStockCountingId(stockCountingId);
            stockCountingDetails.add(dtl);

            StockTotal st = new StockTotal();
            st.setId((long)i);
            st.setShopId(shopId);
            st.setWareHouseTypeId(wareHouseTypeId);
            st.setProductId((long)i);
            st.setQuantity(20);
            st.setStatus(1);
            stockTotals.add(st);

            StockCountingUpdateDTO dtoDtl = new StockCountingUpdateDTO();
            dtoDtl.setProductId((long)i);
            request.add(dtoDtl);
        }
        when(serviceImp.updateStockCounting(stockCountingId, shopId, "admin",request)).thenThrow(ValidateException.class);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions = mockMvc.perform(put(uri, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testCreateStockCounting() throws Exception {
        String uri = V1 + root + "/inventory?override=true";
        Long wareHouseTypeId = 1L;
        Long shopId = 1L;
        List<StockCountingDetailDTO> request = Arrays.asList(new StockCountingDetailDTO(), new StockCountingDetailDTO());
//        given(repository.findByWareHouseTypeId(wareHouseTypeId,shopId,
//                DateUtils.convertFromDate(LocalDateTime.now()), DateUtils.convertToDate(LocalDateTime.now())))
//                .willReturn(new ArrayList<>());
        Long id = service.createStockCounting(request,1L, shopId, wareHouseTypeId, true);
        assertNotNull(id);
        String inputJson = super.mapToJson(request);
        ResultActions resultActions = mockMvc.perform(post(uri)
                .param("wareHouseTypeId", "1")
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}