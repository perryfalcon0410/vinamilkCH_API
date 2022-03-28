package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.StockCountingDetailRepository;
import vn.viettel.sale.repository.StockCountingRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.impl.InventoryServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Mock
    CustomerTypeClient customerTypeClient;

    @Mock
    ProductPriceRepository priceRepository;

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
        serviceImp.index("code", wareHouseTypeId, LocalDateTime.now(), LocalDateTime.now(), 1L, PageRequest.of(2, 5));
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
        Long shopId = 1L;
        Long wareHouseTypeId = 1L;
        String searchKeywords = "KEY";
        List<StockCountingDetailDTO> countingDetails = new ArrayList<>();
        List<CustomerTypeDTO> customerTypes = new ArrayList<>();
        List<Price> prices = new ArrayList<>();
        for(int i = 1; i < 5 ; i++){
            StockCountingDetailDTO  dto = new StockCountingDetailDTO();
            dto.setId((long)i);
            dto.setProductId((long)i);
            dto.setPrice(5500.0);
            dto.setStockQuantity(2);
            countingDetails.add(dto);

            CustomerTypeDTO cDto = new CustomerTypeDTO();
            cDto.setId((long)i);
            customerTypes.add(cDto);

            Price price = new Price();
            price.setId((long)i);
            price.setProductId((long)i);
            price.setPrice(5500.0);
            prices.add(price);
        }

        given(stockTotalRepository.getStockCountingDetail(shopId, wareHouseTypeId, searchKeywords)).willReturn(countingDetails);

        given(customerTypeClient.getCusTypeByWarehouse(wareHouseTypeId)).willReturn(customerTypes);
        List<Long> customerTypeIds = Arrays.asList(-1L);
        if(!customerTypes.isEmpty()) customerTypeIds = customerTypes.stream().map(item -> item.getId()).distinct().collect(Collectors.toList());

        given(priceRepository.findProductPriceWithTypes(countingDetails.stream().map(item -> item.getProductId())
                .collect(Collectors.toList()), customerTypeIds, DateUtils.convertToDate(LocalDateTime.now()))).willReturn(prices);

        Object response = serviceImp.getAll(shopId, searchKeywords, wareHouseTypeId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testGetStockCountingDetailsToDayUpdate() throws Exception {
        String uri = V1 + root + "/inventory/{id}";
        Long shopId = 1L;
        Long id = 1L;

        StockCounting stockCounting = new StockCounting();
        stockCounting.setStockCountingCode("CODE");
        stockCounting.setCountingDate(LocalDateTime.now());
        stockCounting.setWareHouseTypeId(1L);
        given(repository.getByIdAndShopId(id, shopId)).willReturn(stockCounting);

        List<StockCountingDetailDTO> countingDetails = new ArrayList<>();
        List<StockCountingExcelDTO> result = new ArrayList<>();
        for(int i = 1; i < 5 ; i++){
            StockCountingDetailDTO  dto = new StockCountingDetailDTO();
            dto.setId((long)i);
            dto.setProductId((long)i);
            dto.setPrice(5500.0);
            dto.setStockQuantity(2);
            dto.setInventoryQuantity(3);
            dto.setPacketQuantity(3);
            dto.setConvfact(1);
            dto.setUnitQuantity(3);
            dto.setProductCategoryCode("CATCODE");
            dto.setProductCode("CODE");
            countingDetails.add(dto);

            StockCountingExcelDTO excelDTO = new StockCountingExcelDTO();
            excelDTO.setProductId((long)i);
            excelDTO.setProductId((long)i);
            excelDTO.setPrice(5500.0);
            excelDTO.setStockQuantity(2);
            excelDTO.setInventoryQuantity(3);
            excelDTO.setPacketQuantity(3);
            excelDTO.setConvfact(1);
            excelDTO.setUnitQuantity(3);
            excelDTO.setProductCategoryCode("CATCODE");
            excelDTO.setProductCode("CODE");
            result.add(excelDTO);
        }

        given(countingDetailRepository.getStockCountingDetail(id)).willReturn(result);
        given(stockTotalRepository.getStockCountingDetail(shopId, stockCounting.getWareHouseTypeId(), null)).willReturn(countingDetails);
        CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response = serviceImp.getByStockCountingId(id, shopId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testGetStockCountingDetailsToDayNew() throws Exception {
        String uri = V1 + root + "/inventory/{id}";
        Long shopId = 1L;
        Long id = 1L;

        StockCounting stockCounting = new StockCounting();
        stockCounting.setStockCountingCode("CODE");
        stockCounting.setCountingDate(LocalDateTime.now());
        stockCounting.setWareHouseTypeId(1L);
        given(repository.getByIdAndShopId(id, shopId)).willReturn(stockCounting);

        List<StockCountingDetailDTO> countingDetails = new ArrayList<>();
        List<CustomerTypeDTO> customerTypes = new ArrayList<>();
        List<Price> prices = new ArrayList<>();
        List<StockCountingDetailDTO> newProducts = new ArrayList<>();
        for(int i = 1; i < 5 ; i++){
            StockCountingDetailDTO  dto = new StockCountingDetailDTO();
            dto.setId((long)i);
            dto.setProductId((long)i);
            dto.setPrice(5500.0);
            dto.setStockQuantity(2);
            dto.setInventoryQuantity(3);
            dto.setPacketQuantity(3);
            dto.setConvfact(1);
            dto.setUnitQuantity(3);
            dto.setProductCategoryCode("CATCODE");
            dto.setProductCode("CODE");
            countingDetails.add(dto);
            newProducts.add(dto);

            CustomerTypeDTO cDto = new CustomerTypeDTO();
            cDto.setId((long)i);
            customerTypes.add(cDto);

            Price price = new Price();
            price.setId((long)i);
            price.setProductId((long)i);
            price.setPrice(5500.0);
            prices.add(price);
        }
        given(stockTotalRepository.getStockCountingDetail(shopId, stockCounting.getWareHouseTypeId(), null)).willReturn(countingDetails);
        given(customerTypeClient.getCusTypeByWarehouse(stockCounting.getWareHouseTypeId())).willReturn(customerTypes);
        List<Long> customerTypeIds = Arrays.asList(-1L);
        customerTypeIds = customerTypes.stream().map(item -> item.getId()).distinct().collect(Collectors.toList());
        given(priceRepository.findProductPriceWithTypes(newProducts.stream().map(item -> item.getProductId()).distinct()
                .collect(Collectors.toList()), customerTypeIds, DateUtils.convertToDate(LocalDateTime.now()))).willReturn(prices);

        CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response = serviceImp.getByStockCountingId(id, shopId);

        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testGetStockCountingDetailsNotToDay() throws Exception {
        String uri = V1 + root + "/inventory/{id}";
        Long shopId = 1L;
        Long id = 1L;

        StockCounting stockCounting = new StockCounting();
        stockCounting.setStockCountingCode("CODE");
        stockCounting.setCountingDate(LocalDateTime.now().plusDays(-3));
        stockCounting.setWareHouseTypeId(1L);
        given(repository.getByIdAndShopId(id, shopId)).willReturn(stockCounting);

        List<StockCountingDetailDTO> countingDetails = new ArrayList<>();
        List<StockCountingExcelDTO> result = new ArrayList<>();
        for(int i = 1; i < 5 ; i++){
            StockCountingDetailDTO  dto = new StockCountingDetailDTO();
            dto.setId((long)i);
            dto.setProductId((long)i);
            dto.setPrice(5500.0);
            dto.setStockQuantity(2);
            dto.setInventoryQuantity(3);
            dto.setPacketQuantity(3);
            dto.setConvfact(1);
            dto.setUnitQuantity(3);
            dto.setProductCategoryCode("CATCODE");
            dto.setProductCode("CODE");
            countingDetails.add(dto);

            StockCountingExcelDTO excelDTO = new StockCountingExcelDTO();
            excelDTO.setProductId((long)i);
            excelDTO.setProductId((long)i);
            excelDTO.setPrice(5500.0);
            excelDTO.setStockQuantity(2);
            excelDTO.setInventoryQuantity(3);
            excelDTO.setPacketQuantity(3);
            excelDTO.setConvfact(1);
            excelDTO.setUnitQuantity(3);
            excelDTO.setProductCategoryCode("CATCODE");
            excelDTO.setProductCode("CODE");
            result.add(excelDTO);
        }

        given(countingDetailRepository.getStockCountingDetail(id)).willReturn(result);
        CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response = serviceImp.getByStockCountingId(id, shopId);

        assertNotNull(response);

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
        Long shopId = 1L;
        String searchKeywords = "AA";
        MultipartFile firstFile = new MockMultipartFile("filename", "someTestFile.XLSX", "application/vnd.ms-excel", "someTestFile.XLSX".getBytes());

        List<StockCountingDetailDTO> countingDetails = new ArrayList<>();
        List<CustomerTypeDTO> customerTypes = new ArrayList<>();
        List<Price> prices = new ArrayList<>();
        List<StockCountingDetailDTO> newProducts = new ArrayList<>();
        for(int i = 1; i < 5 ; i++){
            StockCountingDetailDTO  dto = new StockCountingDetailDTO();
            dto.setId((long)i);
            dto.setProductId((long)i);
            dto.setPrice(5500.0);
            dto.setStockQuantity(2);
            dto.setInventoryQuantity(3);
            dto.setPacketQuantity(3);
            dto.setConvfact(1);
            dto.setUnitQuantity(3);
            dto.setProductCategoryCode("CATCODE");
            dto.setProductCode("CODE");
            countingDetails.add(dto);
            newProducts.add(dto);

            CustomerTypeDTO cDto = new CustomerTypeDTO();
            cDto.setId((long)i);
            customerTypes.add(cDto);

            Price price = new Price();
            price.setId((long)i);
            price.setProductId((long)i);
            price.setPrice(5500.0);
            prices.add(price);
        }
        CoverResponse<StockCountingImportDTO, InventoryImportInfo> result =
                service.importExcel(shopId, firstFile, PageRequest.of(0, 20), searchKeywords,wareHouseTypeId);

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
        List<StockCounting> stockCountings = new ArrayList<>();
        StockCounting stockCounting = new StockCounting();
        stockCounting.setId(1L);
        stockCounting.setStockCountingCode("12345");
        stockCountings.add(stockCounting);
        Page<StockCounting> stockCountingPage = new PageImpl<>(stockCountings);
        Mockito.when(repository.getLastStockCounting(any(), any(), any())).thenReturn(stockCountingPage);
        Mockito.when(repository.save(any())).thenReturn(stockCounting);
        Long id = serviceImp.createStockCounting(request,1L, shopId, wareHouseTypeId, true);
        assertEquals(stockCounting.getId(), id);
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