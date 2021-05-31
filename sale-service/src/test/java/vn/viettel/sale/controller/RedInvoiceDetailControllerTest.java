package vn.viettel.sale.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.dto.ProductDetailDTO;
import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RedInvoiceDetailControllerTest extends BaseTest{

    private final String root = "/sales";

    @MockBean
    RedInvoiceDetailService redInvoiceDetailService;

    @Test
    public void getRedInvoiceDetailByRedInvoiceId() throws Exception {
        String uri = V1 + root + "/red-invoice-detail/{id}";
        List<RedInvoiceDetailDTO> lists = Arrays.asList(new RedInvoiceDetailDTO(), new RedInvoiceDetailDTO());
        given(redInvoiceDetailService.getRedInvoiceDetailByRedInvoiceId(any())).willReturn(lists);
        ResultActions resultActions = mockMvc.perform(get(uri, 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":["));
    }

}
