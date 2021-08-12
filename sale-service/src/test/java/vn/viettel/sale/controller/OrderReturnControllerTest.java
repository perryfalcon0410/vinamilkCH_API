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
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.messaging.TotalOrderChoose;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderReturnControllerTest extends BaseTest {
    private final String root = "/sales/order-return";

    @MockBean
    private OrderReturnService orderReturnService;

    @Test
    public void getAllOrderReturn() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<OrderReturnDTO> list = Arrays.asList(new OrderReturnDTO(), new OrderReturnDTO());
        Page<OrderReturnDTO> orderReturnDTOS = new PageImpl<>(list, pageRequest , list.size());
        CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse> data =
                new CoverResponse<>(orderReturnDTOS, new SaleOrderTotalResponse());
        given(orderReturnService.getAllOrderReturn(any(), Mockito.any(PageRequest.class), any())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void getOrderReturnDetail() throws Exception {
        String uri = V1 + root + "/detail/{id}";
        OrderReturnDetailDTO data = new OrderReturnDetailDTO();
        given(orderReturnService.getOrderReturnDetail(anyLong())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void selectForReturn() throws Exception {
        String uri = V1 + root + "/choose";
        List<SaleOrderDTO> list = Arrays.asList(new SaleOrderDTO(), new SaleOrderDTO());
        CoverResponse<List<SaleOrderDTO>, TotalOrderChoose> data =
                new CoverResponse<>(list, new TotalOrderChoose());
        given(orderReturnService.getSaleOrderForReturn(any(), any())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void orderSelected() throws Exception {
        String uri = V1 + root + "/chosen/{id}";
        OrderReturnDetailDTO data = new OrderReturnDetailDTO();
        given(orderReturnService.getSaleOrderChosen(any(), any())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }

    @Test
    public void createOrderReturn() throws Exception {
        String uri = V1 + root;
        OrderReturnRequest requestObj = new OrderReturnRequest();
        requestObj.setOrderNumber("SALE.UNITTEST");
//        requestObj.setDateReturn(LocalDateTime.of(2021,3,22,14,29,58));
        requestObj.setReasonId("BREAKITEM");
        requestObj.setReasonDescription("UnitTest");

        SaleOrder dtoObj = new SaleOrder();
        dtoObj.setId(1L);
        dtoObj.setOrderNumber("SALE.UNITTEST");
        dtoObj.setOrderDate(LocalDateTime.of(2021,3,22,14,29,58));
        dtoObj.setShopId(1L);
        dtoObj.setSalemanId(1L);
        dtoObj.setCustomerId(310L);
        dtoObj.setWareHouseTypeId(1L);
        dtoObj.setAmount(-50000D);
        dtoObj.setTotalPromotion(20000D);
        dtoObj.setTotal(-30000D);
        dtoObj.setTotalPaid(-30000D);
        dtoObj.setBalance(0D);
        dtoObj.setNote(requestObj.getReasonDescription());
        dtoObj.setType(2);
        dtoObj.setFromSaleOrderId(1L);
        dtoObj.setAutoPromotionNotVat(0D);
        dtoObj.setAutoPromotionVat(0D);
        dtoObj.setAutoPromotion(0D);
        dtoObj.setZmPromotion(0D);
        dtoObj.setReasonId(requestObj.getReasonId());
        dtoObj.setReasonDesc(requestObj.getReasonDescription());
        given(orderReturnService.createOrderReturn(any(), any(), any())).willReturn(dtoObj);
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(responseData, containsString("\"data\":{"));
    }
}