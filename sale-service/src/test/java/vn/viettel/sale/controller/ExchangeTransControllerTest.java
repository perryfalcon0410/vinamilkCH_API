package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;
import vn.viettel.sale.service.feign.CategoryDataClient;
import vn.viettel.sale.service.feign.CustomerClient;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.impl.ExchangeTranServiceImpl;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ExchangeTransControllerTest extends BaseTest {
    private final String root = "/sales/exchangetrans";
    private final String uri = V1 + root;

    @InjectMocks
    ExchangeTranServiceImpl serviceImp;

    @Mock
    ExchangeTranService service;

    @Mock
    ExchangeTransRepository repository;

    @Mock
    CategoryDataClient categoryDataClient;

    @Mock
    CustomerTypeClient customerTypeClient;

    @Mock
    ProductPriceRepository priceRepository;

    @Mock
    StockTotalRepository stockTotalRepository;

    @Mock
    ExchangeTransDetailRepository transDetailRepository;

    @Mock
    StockTotalService stockTotalService;

    @Mock
    ProductRepository productRepo;

    @Mock
    CustomerClient customerClient;

    private List<ExchangeTrans> exchangeTransList;

    private List<CategoryDataDTO> categoryDataDTOS;

    private List<CustomerTypeDTO> customerTypeDTOS;

    private List<ExchangeTransDetailRequest> exchangeTransDetailRequests;

    private List<Price> priceList;

    private List<StockTotal> stockTotals;

    private List<Product> products;

    private List<CustomerDTO> customerDTOS;

    private List<ExchangeTransDetail> dbExchangeTransDetails;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ExchangeTransController controller = new ExchangeTransController();
        controller.setService(service);
        this.setupAction(controller);

        exchangeTransList = new ArrayList<>();
        final ExchangeTrans exchangeTrans = new ExchangeTrans();
        exchangeTrans.setId(1L);
        exchangeTrans.setCustomerId(1L);
        exchangeTrans.setTransDate(LocalDateTime.now());
        exchangeTrans.setWareHouseTypeId(1L);
        exchangeTransList.add(exchangeTrans);

        categoryDataDTOS = new ArrayList<>();
        final CategoryDataDTO categoryDataDTO = new CategoryDataDTO();
        categoryDataDTO.setId(1L);
        categoryDataDTOS.add(categoryDataDTO);

        customerTypeDTOS = new ArrayList<>();
        final CustomerTypeDTO customerTypeDTO = new CustomerTypeDTO();
        customerTypeDTO.setId(1L);
        customerTypeDTO.setWareHouseTypeId(1L);
        customerTypeDTOS.add(customerTypeDTO);

        exchangeTransDetailRequests = new ArrayList<>();
        final ExchangeTransDetailRequest exchangeTransDetailRequest = new ExchangeTransDetailRequest();
        exchangeTransDetailRequest.setId(1L);
        exchangeTransDetailRequest.setProductId(1L);
        exchangeTransDetailRequest.setPrice(10D);
        exchangeTransDetailRequest.setQuantity(10);
        exchangeTransDetailRequest.setType(2);
        exchangeTransDetailRequests.add(exchangeTransDetailRequest);

        priceList = new ArrayList<>();
        final Price price = new Price();
        price.setId(1L);
        price.setProductId(1L);
        priceList.add(price);

        stockTotals = new ArrayList<>();
        final StockTotal stockTotal = new StockTotal();
        stockTotal.setId(1L);
        stockTotal.setProductId(1L);
        stockTotal.setQuantity(10);
        stockTotal.setStatus(1);
        stockTotal.setShopId(1L);
        stockTotals.add(stockTotal);

        products = new ArrayList<>();
        final Product product = new Product();
        product.setId(1L);
        product.setIsCombo(true);
        product.setStatus(1);
        product.setComboProductId(1L);
        product.setProductCode("1");
        product.setProductName("A");
        products.add(product);

        customerDTOS = new ArrayList<>();
        final CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setFullName("A");
        customerDTO.setAddress("A");
        customerDTO.setMobiPhone("0987654321");
        customerDTOS.add(customerDTO);

        dbExchangeTransDetails = new ArrayList<>();
        final ExchangeTransDetail exchangeTransDetail = new ExchangeTransDetail();
        exchangeTransDetail.setId(1L);
        exchangeTransDetail.setShopId(1L);
        exchangeTransDetail.setProductId(1L);
        exchangeTransDetail.setPrice(10D);
        exchangeTransDetail.setQuantity(10);
        dbExchangeTransDetails.add(exchangeTransDetail);
    }

    //-------------------------------getAllReason-------------------------------
    @Test
    public void getAllReasonTest() throws Exception {
        String url = uri + "/reasons";

        Response<List<CategoryDataDTO>> expected = new Response<>();

        given(categoryDataClient.getByCategoryGroupCodeV1()).willReturn(expected);

        Response<List<CategoryDataDTO>> result = serviceImp.getReasons();

        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getAllExchangeTrans-------------------------------
    @Test
    public void getAllExchangeTransTest() throws Exception {

        Date fromDate = new Date("2022/02/22");
        Date toDate = new Date("2022/02/22");

        Mockito.when(repository.getExchangeTrans(1L, "A", 1, 1L, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate), PageRequest.of(1,5)))
                .thenReturn(new PageImpl<>(exchangeTransList));

        Mockito.when(categoryDataClient.getReasonExchangeFeign(Arrays.asList(-1L), null, null))
                .thenReturn(new Response<List<CategoryDataDTO>>().withData(categoryDataDTOS));

        CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> result =
                serviceImp.getAllExchange(1L, "A", fromDate, toDate, 1L, PageRequest.of(1,5));
        assertNotNull(result);
        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate", "2022/02/22")
                        .param("toDate", "2022/02/22")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------create-------------------------------
    @Test
    public void createTest() throws Exception {
        String url = uri + "/create";

        ExchangeTransRequest exchangeTransRequest = new ExchangeTransRequest();
        exchangeTransRequest.setTransCode("ABC");
        exchangeTransRequest.setCustomerId(1L);
        exchangeTransRequest.setShopId(1L);
        exchangeTransRequest.setReasonId(1L);
        exchangeTransRequest.setLstExchangeDetail(exchangeTransDetailRequests);

        Mockito.when(categoryDataClient.getByCategoryGroupCodeV1())
                .thenReturn(new Response<List<CategoryDataDTO>>().withData(categoryDataDTOS));

        Mockito.when(customerTypeClient.getCustomerTypeForSale(1L, 1L))
                .thenReturn(customerTypeDTOS.get(0));

        LocalDateTime date = LocalDateTime.now();

        List<Long> productIds = exchangeTransDetailRequests.stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());

        Mockito.when(priceRepository.findProductPriceWithType(productIds, customerTypeDTOS.get(0).getId(), DateUtils.convertToDate(date)))
                .thenReturn(priceList);

        Mockito.when(stockTotalRepository.getStockTotal(1L, 1L, productIds))
                .thenReturn(stockTotals);

        ExchangeTrans exchangeTransRecord = new ExchangeTrans();
        exchangeTransRecord.setId(2l);
        Mockito.when(repository.save(Mockito.isA(ExchangeTrans.class))).thenReturn(exchangeTransRecord);
        ExchangeTransDTO result = serviceImp.create(exchangeTransRequest, 1L, 1L);
        assertNotNull(result);

        String inputJson = super.mapToJson(exchangeTransRequest);
        ResultActions resultActions = mockMvc.perform(post(url)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------getExchangeTrans-------------------------------
    @Test
    public void getExchangeTransTest() throws Exception {
        Long id = 1L;
        String url = uri + "/" + id;

        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(exchangeTransList.get(0)));

        List<Long> productIds = exchangeTransDetailRequests.stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());

        Mockito.when(customerClient.getCustomerByIdV1(1L))
                .thenReturn(new Response<CustomerDTO>().withData(customerDTOS.get(0)));

        ExchangeTransDTO exchangeTransDTO = serviceImp.getExchangeTrans(id);

        assertEquals(id, exchangeTransDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------update-------------------------------
    @Test
    public void updateTestFail1() throws Exception {
        Long id = 1L;
        String url = uri + "/update/" + id.toString();

        ExchangeTransRequest exchangeTransRequest = new ExchangeTransRequest();
        exchangeTransRequest.setTransCode("ABC");
        exchangeTransRequest.setCustomerId(1L);
        exchangeTransRequest.setShopId(1L);
        exchangeTransRequest.setReasonId(1L);
        exchangeTransRequest.setId(1L);
        exchangeTransRequest.setTransDate(LocalDateTime.now());
        exchangeTransRequest.setLstExchangeDetail(exchangeTransDetailRequests);

        Mockito.when(repository.getById(id, 1L, 1L)).thenReturn(exchangeTransList.get(0));

        Mockito.when(customerTypeClient.getCustomerTypeForSale(1L, 1L))
                .thenReturn(customerTypeDTOS.get(0));

        LocalDateTime date = LocalDateTime.now();

        List<Long> productIds = exchangeTransDetailRequests.stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());
        productIds.add(2L);

//        Mockito.when(priceRepository.findProductPriceWithType(productIds, customerTypeDTOS.get(0).getId(), DateUtils.convertToDate(date)))
//                .thenReturn(priceList);

//        Mockito.when(stockTotalRepository.getStockTotal(1L, 1L, productIds))
//                .thenReturn(stockTotals);

        Mockito.when(transDetailRepository.findByTransId(id))
                .thenReturn(dbExchangeTransDetails);

        String msgException = "";
        try{
            serviceImp.update(id, exchangeTransRequest, 1L);
        }catch (ValidateException e){
            msgException = e.getMessage();
        }

        assertEquals(String.format(ResponseMessage.PRODUCT_PRICE_NOT_FOUND.statusCodeValue(), "null - null"), msgException );

        String inputJson = super.mapToJson(exchangeTransRequest);
        ResultActions resultActions = mockMvc.perform(put(url, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void update3Test() throws Exception {
        Long id = 1L;
        String url = uri + "/update/" + id.toString();

        exchangeTransDetailRequests.get(0).setType(0);
        ExchangeTransRequest exchangeTransRequest = new ExchangeTransRequest();
        exchangeTransRequest.setTransCode("ABC");
        exchangeTransRequest.setCustomerId(1L);
        exchangeTransRequest.setShopId(1L);
        exchangeTransRequest.setReasonId(1L);
        exchangeTransRequest.setId(1L);
        exchangeTransRequest.setTransDate(LocalDateTime.now());
        exchangeTransRequest.setLstExchangeDetail(exchangeTransDetailRequests);

        Mockito.when(repository.getById(id, 1L, 1L)).thenReturn(exchangeTransList.get(0));

        Mockito.when(customerTypeClient.getCustomerTypeForSale(1L, 1L))
                .thenReturn(customerTypeDTOS.get(0));

        LocalDateTime date = LocalDateTime.now();

        List<Long> productIds = exchangeTransDetailRequests.stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());

        Mockito.when(priceRepository.findProductPriceWithType(productIds, customerTypeDTOS.get(0).getId(), DateUtils.convertToDate(date)))
                .thenReturn(priceList);

        Mockito.when(stockTotalRepository.getStockTotal(1L, 1L, productIds))
                .thenReturn(stockTotals);

        Mockito.when(transDetailRepository.findByTransId(id))
                .thenReturn(dbExchangeTransDetails);

        ExchangeTrans exchange = new ExchangeTrans();
        Mockito.when(repository.save(Mockito.isA(ExchangeTrans.class))).thenReturn(exchange);
        ExchangeTransDTO res = serviceImp.update(id, exchangeTransRequest, 1L);
        assertNotNull(res);

        String inputJson = super.mapToJson(exchangeTransRequest);
        ResultActions resultActions = mockMvc.perform(put(url, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void updateTest() throws Exception {
        Long id = 1L;
        String url = uri + "/update/" + id.toString();

        exchangeTransDetailRequests.get(0).setType(3);
        ExchangeTransRequest exchangeTransRequest = new ExchangeTransRequest();
        exchangeTransRequest.setTransCode("ABC");
        exchangeTransRequest.setCustomerId(1L);
        exchangeTransRequest.setShopId(1L);
        exchangeTransRequest.setReasonId(1L);
        exchangeTransRequest.setId(1L);
        exchangeTransRequest.setTransDate(LocalDateTime.now());
        exchangeTransRequest.setLstExchangeDetail(exchangeTransDetailRequests);

        Mockito.when(repository.getById(id, 1L, 1L)).thenReturn(exchangeTransList.get(0));

        Mockito.when(customerTypeClient.getCustomerTypeForSale(1L, 1L))
                .thenReturn(customerTypeDTOS.get(0));

        LocalDateTime date = LocalDateTime.now();

        List<Long> productIds = exchangeTransDetailRequests.stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());

        Mockito.when(priceRepository.findProductPriceWithType(productIds, customerTypeDTOS.get(0).getId(), DateUtils.convertToDate(date)))
                .thenReturn(priceList);

        Mockito.when(stockTotalRepository.getStockTotal(1L, 1L, productIds))
                .thenReturn(stockTotals);

        Mockito.when(transDetailRepository.findByTransId(id))
                .thenReturn(dbExchangeTransDetails);

        ExchangeTrans exchange = new ExchangeTrans();
        Mockito.when(repository.save(Mockito.isA(ExchangeTrans.class))).thenReturn(exchange);
        ExchangeTransDTO res = serviceImp.update(id, exchangeTransRequest, 1L);
        assertNotNull(res);

        String inputJson = super.mapToJson(exchangeTransRequest);
        ResultActions resultActions = mockMvc.perform(put(url, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void update2Test() throws Exception {
        Long id = 1L;
        String url = uri + "/update/" + id.toString();

        ExchangeTransRequest exchangeTransRequest = new ExchangeTransRequest();
        exchangeTransRequest.setTransCode("ABC");
        exchangeTransRequest.setCustomerId(1L);
        exchangeTransRequest.setShopId(1L);
        exchangeTransRequest.setReasonId(1L);
        exchangeTransRequest.setId(1L);
        exchangeTransRequest.setTransDate(LocalDateTime.now());
        exchangeTransRequest.setLstExchangeDetail(exchangeTransDetailRequests);

        Mockito.when(repository.getById(id, 1L, 1L)).thenReturn(exchangeTransList.get(0));

        Mockito.when(customerTypeClient.getCustomerTypeForSale(1L, 1L))
                .thenReturn(customerTypeDTOS.get(0));

        LocalDateTime date = LocalDateTime.now();

        List<Long> productIds = exchangeTransDetailRequests.stream().map(
                item -> item.getProductId()).distinct().collect(Collectors.toList());

        Mockito.when(priceRepository.findProductPriceWithType(productIds, customerTypeDTOS.get(0).getId(), DateUtils.convertToDate(date)))
                .thenReturn(priceList);

        Mockito.when(stockTotalRepository.getStockTotal(1L, 1L, productIds))
                .thenReturn(stockTotals);

        Mockito.when(transDetailRepository.findByTransId(id))
                .thenReturn(dbExchangeTransDetails);

        ExchangeTrans exchange = new ExchangeTrans();
        exchange.setId(2l);
        Mockito.when(repository.save(Mockito.isA(ExchangeTrans.class))).thenReturn(exchange);
        ExchangeTransDTO res = serviceImp.update(id, exchangeTransRequest, 1L);
        assertNotNull(res);

        String inputJson = super.mapToJson(exchangeTransRequest);
        ResultActions resultActions = mockMvc.perform(put(url, 1L)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
