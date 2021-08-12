package vn.viettel.promotion.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.promotion.BaseTest;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.service.VoucherService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class VoucherControllerTest extends BaseTest {
    private final String root = "/promotions/vouchers";
    private final String uri = V1 + root;

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @MockBean
    private VoucherService voucherService;

    //-------------------------------updateVoucher-------------------------------
    @Test
    public void updateVoucherTest() throws Exception {

        VoucherDTO data = new VoucherDTO();
        data.setId(1L);
        data.setActivated(true);
        data.setCustomerTypeId(1L);
        data.setStatus(1);
        data.setChangeUser("Tuan");
        data.setCustomerId(1L);
        data.setIsUsed(true);
        data.setOrderAmount(100000D);

        given(voucherService.updateVoucher(any())).willReturn(data);
        String inputJson = super.mapToJson(data);
        ResultActions resultActions = mockMvc.perform(put(uri)
                .header(headerType, secretKey)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------getVoucherBySaleOrderId-------------------------------
    @Test
    public void getVoucherBySaleOrderIdTest() throws Exception {
        String url = uri + "/get-by-sale-order-id/{id}";
        List<VoucherDTO> data = new ArrayList<>();
        data.add(new VoucherDTO());

        given(voucherService.getVoucherBySaleOrderId(anyLong())).willReturn(data);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }
}
