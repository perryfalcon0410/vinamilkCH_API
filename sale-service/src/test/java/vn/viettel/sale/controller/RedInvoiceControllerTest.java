//package vn.viettel.sale.controller;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import vn.viettel.core.messaging.CoverResponse;
//import vn.viettel.core.messaging.Response;
//import vn.viettel.core.util.DateUtils;
//import vn.viettel.core.util.ResponseMessage;
//import vn.viettel.sale.BaseTest;
//import vn.viettel.sale.entities.RedInvoice;
//import vn.viettel.sale.messaging.RedInvoiceRequest;
//import vn.viettel.sale.messaging.TotalRedInvoice;
//import vn.viettel.sale.messaging.TotalRedInvoiceResponse;
//import vn.viettel.sale.repository.RedInvoiceRepository;
//import vn.viettel.sale.service.ProductService;
//import vn.viettel.sale.service.RedInvoiceService;
//import vn.viettel.sale.service.SaleOrderService;
//import vn.viettel.sale.service.dto.*;
//import vn.viettel.sale.service.feign.CustomerClient;
//import vn.viettel.sale.service.impl.RedInvoiceServiceImpl;
//import vn.viettel.sale.specification.RedInvoiceSpecification;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class RedInvoiceControllerTest extends BaseTest {
//    private final String root = "/sales/red-invoices";
//
//    @InjectMocks
//    RedInvoiceServiceImpl serviceImp;
//
//    @Mock
//    RedInvoiceService service;
//
//    @Mock
//    RedInvoiceRepository repository;
//
//    @Mock
//    SaleOrderService saleOrderService;
//
//    @Mock
//    ProductService productService;
//
//    @Mock
//    CustomerClient customerClient;
//
//    @Before
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//        serviceImp.setModelMapper(this.modelMapper);
//        final RedInvoiceController controller = new RedInvoiceController();
//        controller.setService(service);
//        this.setupAction(controller);
//    }
//
//    @Test
//    public void findALlProductInfoTest() throws Exception {
//        String uri = V1 + root ;
//        String searchKeywords = null;
//        Date fromDate = new Date();
//        Date toDate = new Date();
//        LocalDateTime tsFromDate = DateUtils.convertFromDate(fromDate);
//        LocalDateTime tsToDate = DateUtils.convertToDate(toDate);
//        tsFromDate = DateUtils.convertFromDate(fromDate);
//        tsToDate = DateUtils.convertToDate(toDate);
//        Long shopId = 1L;
//        String invoiceNumber = "INVOICENO123";
//        List<Long> ids = Arrays.asList(-1L);
//        Response res = new Response();
//        res.withData(ids);
//        Pageable pageable = PageRequest.of(1, 5);
//        List<RedInvoice> redInvoices = new ArrayList<>();
//        for(int i = 1; i < 5; i++){
//            RedInvoice redInvoice = new RedInvoice();
//            redInvoices.add(redInvoice);
//        }
//        Page<RedInvoice> pageRedInvoices = new PageImpl<RedInvoice>(redInvoices, pageable, redInvoices.size());
//
////        given(customerClient.getIdCustomerBySearchKeyWordsV1(searchKeywords.trim())).willReturn(res);
//        given(repository.findAll(Specification.where(RedInvoiceSpecification.hasCustomerId(ids))
//                .and(RedInvoiceSpecification.hasShopId(shopId))
//                .and(RedInvoiceSpecification.hasFromDateToDate(fromDate, toDate))
//                .and(RedInvoiceSpecification.hasInvoiceNumber(invoiceNumber)), pageable)).willReturn(pageRedInvoices);
//        TotalRedInvoice total = new TotalRedInvoice();
//        total.setSumAmountNotVat(2.0);
//        total.setSumAmountGTGT(0.0);
//        given(repository.getTotalRedInvoice1(shopId, ids, invoiceNumber, tsFromDate, tsToDate)).willReturn(total);
//        given(repository.getTotalRedInvoice2(shopId, ids, invoiceNumber, tsFromDate, tsToDate)).willReturn(total);
//
//        serviceImp.getAll(shopId, searchKeywords, fromDate, toDate, invoiceNumber, pageable);
//
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
//    }
//
////    @Test
////    public void getAllBillOfSaleList() throws Exception {
////        String uri = V1 + root + "/bill-of-sale-list";
////        int size = 2;
////        int page = 5;
////        PageRequest pageRequest = PageRequest.of(page, size);
////        List<SaleOrderDTO> lstDto = Arrays.asList(new SaleOrderDTO(), new SaleOrderDTO());
////        Page<SaleOrderDTO> pageDto = new PageImpl<>(lstDto, pageRequest, lstDto.size());
////        given(saleOrderService.getAllBillOfSaleList(any(), any(), Mockito.any(PageRequest.class))).willReturn(pageDto);
////        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
////        String responseData = resultActions.andReturn().getResponse().getContentAsString();
////        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
////
////    }
////
////    @Test
////    public void getDataInBillOfSale() throws Exception {
////        String uri = V1 + root + "/show-invoice-details";
////        List<RedInvoiceDataDTO> list = Arrays.asList(new RedInvoiceDataDTO(), new RedInvoiceDataDTO());
////        CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> data =
////                new CoverResponse<>(list, new TotalRedInvoiceResponse());
////        given(redInvoiceService.getDataInBillOfSale(any(), any())).willReturn(data);
////
////        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
////        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
////    }
////
////    @Test
////    public void getAllProductByOrderNumber() throws Exception {
////        String uri = V1 + root + "/show-info-product";
////        List<ProductDetailDTO> productDetailDTOS = Arrays.asList(new ProductDetailDTO(), new ProductDetailDTO());
////        given(redInvoiceService.getAllProductByOrderNumber(any())).willReturn(productDetailDTOS);
////        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
////        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
////    }
////
//////    @Test
//////    public void create() throws Exception {
//////        String uri = V1 + root + "/create";
//////        RedInvoiceNewDataDTO request = new RedInvoiceNewDataDTO();
//////        request.setShopId(1L);
//////        request.setCustomerId(1L);
//////        request.setRedInvoiceNumber("NB001");
//////        request.setOfficeAddress("Tp.HCM");
//////        request.setTaxCode("TAX_CODE");
//////        ResponseMessage response = ResponseMessage.SUCCESSFUL;
//////        given(redInvoiceService.create(any(), any(), any())).willReturn(response);
//////        String inputJson = super.mapToJson(request);
//////        ResultActions resultActions =  mockMvc
//////                .perform(MockMvcRequestBuilders.post(uri)
//////                        .content(inputJson)
//////                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//////                .andDo(MockMvcResultHandlers.print());
//////        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//////        MvcResult mvcResult = resultActions.andReturn();
//////        assertEquals(200, mvcResult.getResponse().getStatus());
//////    }
////
////    @Test
////    public void searchProduct() throws Exception {
////        String uri = V1 + root + "/search-product";
////        List<ProductDataSearchDTO> productDataSearchDTOS = Arrays.asList(new ProductDataSearchDTO(), new ProductDataSearchDTO());
////        given(productService.findAllProduct(any(), any())).willReturn(productDataSearchDTOS);
////        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
////        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
////    }
////
////
////    @Test
////    public void delete() throws Exception {
////        String uri = V1 + root + "/delete";
////
////        ResponseMessage message =  ResponseMessage.SUCCESSFUL;
////        given(redInvoiceService.deleteByIds(any())).willReturn(message);
////        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(uri).contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
////        String responseData = resultActions.andReturn().getResponse().getContentAsString();
////        MvcResult mvcResult = resultActions.andReturn();
////        assertEquals(200, mvcResult.getResponse().getStatus());
////    }
////
////    @Test
////    public void update() throws Exception {
////        String uri = V1 + root + "/update";
////        List<RedInvoiceRequest> redInvoiceRequests = Arrays.asList(new RedInvoiceRequest(), new RedInvoiceRequest());
////        ResponseMessage message =  ResponseMessage.SUCCESSFUL;
////        given(redInvoiceService.updateRed(any(), any(), any())).willReturn(message);
////        String inputJson = super.mapToJson(redInvoiceRequests);
////        ResultActions resultActions =  mockMvc.perform(MockMvcRequestBuilders.put(uri)
////                .content(inputJson)
////                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
////                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
////        String responseData = resultActions.andReturn().getResponse().getContentAsString();
////        MvcResult mvcResult = resultActions.andReturn();
////        assertEquals(200, mvcResult.getResponse().getStatus());
////    }
//
//
//}
