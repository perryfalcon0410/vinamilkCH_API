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
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OnlineOrderControllerTest extends BaseTest {
    private final String root = "/sales/online-orders";

    @MockBean
    private OnlineOrderService onlineOrderService;

    @Test
    public void findOnlineOrdersSuccessTest() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<OnlineOrderDTO> list = Arrays.asList(new OnlineOrderDTO(), new OnlineOrderDTO());
        Page<OnlineOrderDTO> onlineOrderDTOS = new PageImpl<>(list, pageRequest , list.size());

        given(onlineOrderService.getOnlineOrders(any(), any())).willReturn(onlineOrderDTOS);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    @Test
    public void getOnlineOrderSuccessTest() throws Exception {
        String uri = V1 + root + "/{id}";
        OnlineOrderDTO data = new OnlineOrderDTO();
        data.setId(1L);
        data.setQuantity(12);
        data.setOrderNumber("123");
        given(onlineOrderService.getOnlineOrder(any(), any(), any())).willReturn(data);
        ResultActions resultActions = mockMvc.perform(get(uri, "123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }
}
