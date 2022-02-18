//package vn.viettel.sale.controller;
//
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import vn.viettel.core.messaging.CoverResponse;
//import vn.viettel.sale.BaseTest;
//import vn.viettel.sale.messaging.SaleOrderTotalResponse;
//import vn.viettel.sale.messaging.TotalResponse;
//import vn.viettel.sale.service.SaleOrderService;
//import vn.viettel.sale.service.dto.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.Assert.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class SaleControllerTest extends BaseTest {
//
//    private final String root = "/sales/sale-orders";
//
//    @MockBean
//    SaleOrderService saleOrderService;
//
//    @Test
//    public void getAllSaleOrder() throws Exception {
//        String uri = V1 + root;
//        int size = 2;
//        int page = 5;
//        PageRequest pageRequest = PageRequest.of(page, size);
//        List<SaleOrderDTO> list = Arrays.asList(new SaleOrderDTO(), new SaleOrderDTO());
//        Page<SaleOrderDTO> redInvoiceDTOs = new PageImpl<>(list, pageRequest , list.size());
//        CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> data =
//                new CoverResponse<>(redInvoiceDTOs, new SaleOrderTotalResponse());
//
//        given(saleOrderService.getAllSaleOrder(any(), Mockito.any(PageRequest.class), any())).willReturn(data);
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
//    public void getSaleOrderDetail() throws Exception {
//        String uri = V1 + root + "?saleOrderId=1&orderNumber=OD12";
//        SaleOrderDetailDTO saleOrderDetail = new SaleOrderDetailDTO();
//        saleOrderDetail.setDiscount(Arrays.asList(new DiscountDTO(), new DiscountDTO()));
//        given(saleOrderService.getSaleOrderDetail(anyLong(), any())).willReturn(saleOrderDetail);
//
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//    }
//
//    @Test
//    public void printSaleOrder() throws Exception {
//        String uri = V1 + root + "/print-sale-order/{id}";
//        PrintSaleOrderDTO dto = new PrintSaleOrderDTO();
//
//        given(saleOrderService.printSaleOrder(anyLong(), any())).willReturn(dto);
//
//        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"data\":{"));
//    }
//
//}
