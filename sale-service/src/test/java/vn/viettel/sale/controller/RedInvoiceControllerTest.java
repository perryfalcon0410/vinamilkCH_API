//package vn.viettel.sale.controller;
//
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import vn.viettel.core.messaging.CoverResponse;
//import vn.viettel.core.util.ResponseMessage;
//import vn.viettel.sale.BaseTest;
//import vn.viettel.sale.messaging.RedInvoiceRequest;
//import vn.viettel.sale.messaging.TotalRedInvoice;
//import vn.viettel.sale.messaging.TotalRedInvoiceResponse;
//import vn.viettel.sale.service.ProductService;
//import vn.viettel.sale.service.RedInvoiceService;
//import vn.viettel.sale.service.SaleOrderService;
//import vn.viettel.sale.service.dto.*;
//
//import java.util.Arrays;
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
//    @MockBean
//    RedInvoiceService redInvoiceService;
//
//    @MockBean
//    SaleOrderService saleOrderService;
//
//    @MockBean
//    ProductService productService;
//
//    @Test
//    public void findALlProductInfo() throws Exception {
//        String uri = V1 + root ;
//        int size = 2;
//        int page = 5;
//        PageRequest pageRequest = PageRequest.of(page, size);
//        List<RedInvoiceDTO> list = Arrays.asList(new RedInvoiceDTO(), new RedInvoiceDTO());
//        Page<RedInvoiceDTO> redInvoiceDTOs = new PageImpl<>(list, pageRequest , list.size());
//        CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice> data =
//                new CoverResponse<>(redInvoiceDTOs, new TotalRedInvoice());
//        given(redInvoiceService.getAll(any(), any(), any(),  any(),  any(), Mockito.any(PageRequest.class))).willReturn(data);
//
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"pageNumber\":" + page));
//        assertThat(responseData, containsString("\"pageSize\":" + size));
//    }
//
//    @Test
//    public void getAllBillOfSaleList() throws Exception {
//        String uri = V1 + root + "/bill-of-sale-list";
//        int size = 2;
//        int page = 5;
//        PageRequest pageRequest = PageRequest.of(page, size);
//        List<SaleOrderDTO> lstDto = Arrays.asList(new SaleOrderDTO(), new SaleOrderDTO());
//        Page<SaleOrderDTO> pageDto = new PageImpl<>(lstDto, pageRequest, lstDto.size());
//        given(saleOrderService.getAllBillOfSaleList(any(), any(), Mockito.any(PageRequest.class))).willReturn(pageDto);
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"pageNumber\":" + page));
//        assertThat(responseData, containsString("\"pageSize\":" + size));
//
//    }
//
//    @Test
//    public void getDataInBillOfSale() throws Exception {
//        String uri = V1 + root + "/show-invoice-details";
//        List<RedInvoiceDataDTO> list = Arrays.asList(new RedInvoiceDataDTO(), new RedInvoiceDataDTO());
//        CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> data =
//                new CoverResponse<>(list, new TotalRedInvoiceResponse());
//        given(redInvoiceService.getDataInBillOfSale(any(), any())).willReturn(data);
//
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"data\":{"));
//    }
//
//    @Test
//    public void getAllProductByOrderNumber() throws Exception {
//        String uri = V1 + root + "/show-info-product";
//        List<ProductDetailDTO> productDetailDTOS = Arrays.asList(new ProductDetailDTO(), new ProductDetailDTO());
//        given(redInvoiceService.getAllProductByOrderNumber(any())).willReturn(productDetailDTOS);
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"data\":["));
//    }
//
////    @Test
////    public void create() throws Exception {
////        String uri = V1 + root + "/create";
////        RedInvoiceNewDataDTO request = new RedInvoiceNewDataDTO();
////        request.setShopId(1L);
////        request.setCustomerId(1L);
////        request.setRedInvoiceNumber("NB001");
////        request.setOfficeAddress("Tp.HCM");
////        request.setTaxCode("TAX_CODE");
////        ResponseMessage response = ResponseMessage.SUCCESSFUL;
////        given(redInvoiceService.create(any(), any(), any())).willReturn(response);
////        String inputJson = super.mapToJson(request);
////        ResultActions resultActions =  mockMvc
////                .perform(MockMvcRequestBuilders.post(uri)
////                        .content(inputJson)
////                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
////                .andDo(MockMvcResultHandlers.print());
////        String responseData = resultActions.andReturn().getResponse().getContentAsString();
////        MvcResult mvcResult = resultActions.andReturn();
////        assertEquals(200, mvcResult.getResponse().getStatus());
////    }
//
//    @Test
//    public void searchProduct() throws Exception {
//        String uri = V1 + root + "/search-product";
//        List<ProductDataSearchDTO> productDataSearchDTOS = Arrays.asList(new ProductDataSearchDTO(), new ProductDataSearchDTO());
//        given(productService.findAllProduct(any(), any())).willReturn(productDataSearchDTOS);
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"data\":["));
//    }
//
//
//    @Test
//    public void delete() throws Exception {
//        String uri = V1 + root + "/delete";
//
//        ResponseMessage message =  ResponseMessage.SUCCESSFUL;
//        given(redInvoiceService.deleteByIds(any())).willReturn(message);
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//    }
//
//    @Test
//    public void update() throws Exception {
//        String uri = V1 + root + "/update";
//        List<RedInvoiceRequest> redInvoiceRequests = Arrays.asList(new RedInvoiceRequest(), new RedInvoiceRequest());
//        ResponseMessage message =  ResponseMessage.SUCCESSFUL;
//        given(redInvoiceService.updateRed(any(), any(), any())).willReturn(message);
//        String inputJson = super.mapToJson(redInvoiceRequests);
//        ResultActions resultActions =  mockMvc.perform(MockMvcRequestBuilders.put(uri)
//                .content(inputJson)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//    }
//
//
//}
