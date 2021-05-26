package vn.viettel.sale.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderReturnControllerTest extends BaseTest{
    private final String root = "/sales/order-return";

    @MockBean
    private OrderReturnService orderReturnService;

    @Test
    public void getAllOrderReturn() throws Exception{
        String uri = V1 + root;
        int size = 1;
        int page = 0;
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
    public void getOrderReturnDetail() {
    }

    @Test
    public void selectForReturn() {
    }

    @Test
    public void orderSelected() {
    }

    @Test
    public void createOrderReturn() {
    }
}