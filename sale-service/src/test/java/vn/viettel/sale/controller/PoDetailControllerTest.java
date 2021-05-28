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
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.sale.service.PoDetailService;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PoDetailControllerTest extends BaseTest{
    private final String root = "/sales/po-details/po-confirm-id/{id}";
    @MockBean
    private PoDetailService poDetailService;

    @Test
    public void getAllbyPoConFirmId() throws Exception {
        String uri = V1 + root;
        int size = 2;
        int page = 5;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<PoDetail> list = Arrays.asList(new PoDetail(), new PoDetail());
        Page<PoDetail> orderReturnDTOS = new PageImpl<>(list, pageRequest , list.size());
        Response<Page<PoDetail>> response = new Response<>();
        response.setData(orderReturnDTOS);
        given(poDetailService.getAllByPoConfirmId(any(), Mockito.any(PageRequest.class))).willReturn(response);
        ResultActions resultActions = mockMvc.perform(get(uri,1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }
}