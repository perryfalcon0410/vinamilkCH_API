package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.OnlineOrderService;
import vn.viettel.sale.service.dto.OnlineOrderDTO;
import vn.viettel.sale.service.impl.OnlineOrderServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OnlineOrderControllerTest extends BaseTest {
    private final String root = "/sales/online-orders";

    @InjectMocks
    OnlineOrderServiceImpl serviceImp;

    @Mock
    OnlineOrderService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final OnlineOrderController controller = new OnlineOrderController();
        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void findOnlineOrdersSuccessTest() throws Exception{
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<OnlineOrderDTO> list = Arrays.asList(new OnlineOrderDTO(), new OnlineOrderDTO());
        Page<OnlineOrderDTO> onlineOrderDTOS = service.getOnlineOrders(any(), any());

//        given(serviceImp.getOnlineOrders(any(), any())).willReturn(onlineOrderDTOS);
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getOnlineOrderSuccessTest() throws Exception {
        String uri = V1 + root + "/{id}";
        OnlineOrderDTO data = new OnlineOrderDTO();
        data.setId(1L);
        data.setQuantity(12);
        data.setOrderNumber("123");
//        given(service.getOnlineOrder(any(), any(), any())).willReturn(data);
        service.getOnlineOrder(any(), any(), any());
        ResultActions resultActions = mockMvc.perform(get(uri, "123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
