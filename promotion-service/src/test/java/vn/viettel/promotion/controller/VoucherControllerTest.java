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
import vn.viettel.core.messaging.Response;
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

    //-------------------------------findVouchers-------------------------------
    @Test
    public void findVouchersTest() throws Exception {
        Response<Page<VoucherDTO>> result = new Response<>();
        List<VoucherDTO> data = new ArrayList<>();
        data.add(new VoucherDTO());

        result.setData(new PageImpl<>(data));

        given(voucherService.findVouchers(any(), any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .header(headerType, secretKey)
                .param("keyWord", "KEY")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------getVoucher-------------------------------
    @Test
    public void getVoucherTest() throws Exception {
        String url = uri + "/{id}";

        Response<VoucherDTO> result = new Response<>();
        VoucherDTO data = new VoucherDTO();

        result.setData(data);

        given(voucherService.getVoucher(any(), any(), any(), any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .header(headerType, secretKey)
                .param("productIds", "1")
                .param("productIds", "2")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------getFeignVoucher-------------------------------
    @Test
    public void getFeignVoucherTest() throws Exception {
        String url = uri + "/feign/{id}";

        Response<Voucher> result = new Response<>();
        Voucher data = new Voucher();

        result.setData(data);

        given(voucherService.getFeignVoucher(any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

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
        data.setOrderAmount(100000F);

        given(voucherService.updateVoucher(any())).willReturn(new Response<VoucherDTO>().withData(data));
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

    //-------------------------------findVoucherSaleProducts-------------------------------
    @Test
    public void findVoucherSaleProductsTest() throws Exception {
        String url = uri + "/voucher-sale-products/{voucherProgramId}";

        Response<List<VoucherSaleProductDTO>> result = new Response<>();
        List<VoucherSaleProductDTO> data = new ArrayList<>();
        data.add(new VoucherSaleProductDTO());
        result.setData(data);

        given(voucherService.findVoucherSaleProducts(anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------getVoucherBySaleOrderId-------------------------------
    @Test
    public void getVoucherBySaleOrderIdTest() throws Exception {
        String url = uri + "/get-by-sale-order-id/{id}";

        Response<List<VoucherDTO>> result = new Response<>();
        List<VoucherDTO> data = new ArrayList<>();
        data.add(new VoucherDTO());
        result.setData(data);

        given(voucherService.getVoucherBySaleOrderId(anyLong())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .header(headerType, secretKey)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }
}
