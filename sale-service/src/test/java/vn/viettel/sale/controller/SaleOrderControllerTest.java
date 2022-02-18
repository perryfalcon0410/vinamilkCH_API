//package vn.viettel.sale.controller;
//
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import vn.viettel.core.dto.common.ApParamDTO;
//import vn.viettel.core.messaging.CoverResponse;
//import vn.viettel.sale.BaseTest;
//import vn.viettel.sale.messaging.SaleOrderTotalResponse;
//import vn.viettel.sale.service.SaleOrderService;
//import vn.viettel.sale.service.dto.PrintSaleOrderDTO;
//import vn.viettel.sale.service.dto.SaleOrderDTO;
//import vn.viettel.sale.service.dto.SaleOrderDetailDTO;
//
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.Assert.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class SaleOrderControllerTest extends BaseTest {
//    private final String root = "/sales/sale-orders";
//
//    @MockBean
//    SaleOrderService saleOrderService;
//
//    @Test
//    public void getAllSaleOrder() throws Exception{
//        String uri = V1 + root;
//
//        int size = 2;
//        int page = 2;
//        PageRequest pageReq = PageRequest.of(page, size);
//        List<SaleOrderDTO> lstDto = Arrays.asList(new SaleOrderDTO(), new SaleOrderDTO());
//        Page<SaleOrderDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
//        CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> response = new CoverResponse<>(pageDto, new SaleOrderTotalResponse());
//        given(saleOrderService.getAllSaleOrder(any(),Mockito.any(PageRequest.class), any())).willReturn(response);
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
//    public void getSaleOrderDetail() throws Exception{
//        String uri = V1 + root +"/detail";
//
//        given(saleOrderService.getSaleOrderDetail(anyLong(), any()))
//                .willReturn(new SaleOrderDetailDTO());
//
//        ResultActions resultActions = mockMvc.perform(get(uri)
//                .param("saleOrderId" , "1")
//                .param("orderNumber", "OD1234")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
//
//    @Test
//    public void getLastSaleOrderByCustomerId() throws Exception{
//        String uri = V1 + root +"/last-sale-order/{id}";
//
//        given(saleOrderService.getLastSaleOrderByCustomerId(anyLong()))
//                .willReturn(new SaleOrderDTO());
//
//        ResultActions resultActions = mockMvc.perform(get(uri,1)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
//
//    @Test
//    public void printSaleOrder() throws Exception{
//        String uri = V1 + root +"/print-sale-order/{id}";
//
//        given(saleOrderService.printSaleOrder(any(),any()))
//                .willReturn(new PrintSaleOrderDTO());
//
//        ResultActions resultActions = mockMvc.perform(get(uri,1)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
////        resultActions.andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
//}