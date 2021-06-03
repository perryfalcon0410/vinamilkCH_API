package vn.viettel.customer.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.service.RptCusMemAmountService;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class RptCusMemAmountControllerTest extends BaseTest {

    private final String root = "/customers/prt-cus-mem-amounts";

    @MockBean
    private RptCusMemAmountService rptCusMemAmountService;

    //-------------------------------findMemberCardById---------------------------
    @Test
    public void findRptCusMemAmountByCustomerIdSuccessV1Test() throws Exception {

        String uri = V1 + root + "/customer-id/{id}";
        RptCusMemAmountDTO dtoObj = new RptCusMemAmountDTO();
        dtoObj.setId(1L);
        dtoObj.setCustomerTypeId(1L);
        dtoObj.setAmount(100000F);
        dtoObj.setQuantity(10);
        dtoObj.setScore(23);

        given( rptCusMemAmountService.findByCustomerId(any())).willReturn(dtoObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.get(uri, 1)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }
}
