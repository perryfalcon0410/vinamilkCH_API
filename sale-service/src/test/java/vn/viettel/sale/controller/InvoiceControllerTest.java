package vn.viettel.sale.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.ReportProductTransService;
import vn.viettel.sale.service.dto.ReportProductTransDTO;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InvoiceControllerTest extends BaseTest {
    private final String root = "/sales/invoices";

    @MockBean
    private ReportProductTransService reportProductTransService;

    @Test
    public void findComboProductsSuccessTest() throws Exception {
        String uri = V1 + root + "/product-trans/{transCode}";
        ReportProductTransDTO data = new ReportProductTransDTO();
        data.setId(1L);
        given(reportProductTransService.getInvoice(any(), any())).willReturn(new Response<ReportProductTransDTO>().withData(data));
        ResultActions resultActions = mockMvc.perform(get(uri, "123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"data\":{"));
    }
}
