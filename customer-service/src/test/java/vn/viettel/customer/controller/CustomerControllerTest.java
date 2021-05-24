package vn.viettel.customer.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CustomerRequest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.service.CustomerService;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomerControllerTest extends BaseTest {

    private final String root = "/customers";

    @MockBean
    private CustomerService customerService;

    //-------------------------------GetAllCustomer-------------------------------
    @Test
    public void getAllCustomer() throws Exception {
        String uri = V1 + root;
        int size = 2;
        int page = 5;

        PageRequest pageReq = PageRequest.of(page, size);
        List<CustomerDTO> lstDto = Arrays.asList(new CustomerDTO(), new CustomerDTO());
        Page<CustomerDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());

        given(customerService.index(any(),Mockito.any(PageRequest.class))).willReturn(new Response<Page<CustomerDTO>>().withData(pageDto));

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        String responeData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responeData, containsString("\"pageNumber\":" + page));
        assertThat(responeData, containsString("\"pageSize\":" + size));
    }

    //-------------------------------UpdateCustomer-------------------------------


    //-------------------------------CreateCustomer-------------------------------
    @Test
    public void createCustomerSuccessV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1L);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("AUTO TEST");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void createCustomerRequiredFirstNameV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setLastName("Last");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1L);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7001"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }

    @Test
    public void createCustomerRequiredLastNameV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1L);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7000"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }

    @Test
    public void createCustomerRequiredDOBV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        requestObj.setLastName("Last");
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1L);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7033"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }

    @Test
    public void createCustomerRequiredStatusV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        requestObj.setLastName("Last");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setMobiPhone("0982222428");
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7003"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }

    @Test
    public void createCustomerRequiredMobiPhoneV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        requestObj.setLastName("Last");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setStatus(1L);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7015"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }

    @Test
    public void createCustomerRequiredCustomerTypeV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        requestObj.setLastName("Last");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setStatus(1L);
        requestObj.setMobiPhone("0941111111");
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7041"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }

    @Test
    public void createCustomerRequiredStreetV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        requestObj.setLastName("Last");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setStatus(1L);
        requestObj.setMobiPhone("0941111111");
        requestObj.setAreaId(51L);
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7040"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }

    @Test
    public void createCustomerRequiredAreaV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        requestObj.setLastName("Last");
        Calendar cal = Calendar.getInstance();
        cal.set(2010,3,22,14,29,58);
        requestObj.setDob(cal.getTime());
        requestObj.setStatus(1L);
        requestObj.setMobiPhone("0941111111");
        requestObj.setStreet("51L");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        Timestamp ts = new Timestamp(new Date().getTime());
        dtoObj.setCreatedAt(ts);
        dtoObj.setUpdatedAt(ts);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(new Response<CustomerDTO>().withData(dtoObj));
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"statusCode\":7022"));
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("\"data\":null"));
    }
}
