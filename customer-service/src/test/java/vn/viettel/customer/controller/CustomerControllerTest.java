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
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.service.CustomerService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

        given(customerService.index(any(),Mockito.any(PageRequest.class))).willReturn(pageDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        String responeData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responeData, containsString("\"pageNumber\":" + page));
        assertThat(responeData, containsString("\"pageSize\":" + size));
    }

    //-------------------------------UpdateCustomer-------------------------------
    @Test
    public void updateCustomerSuccessV1Test() throws Exception {
        String uri = V1 + root + "/update/{id}";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("AUTO TEST");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.update((CustomerRequest) any(), any(), any(),any())).willReturn(dtoObj);
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.patch(uri, 1)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------FindById-------------------------------------
    @Test
    public void findCustomerByIdSuccessV1Test() throws Exception {
        String uri = V1 + root + "/{id}";

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName("requestObj.getFirstName()");
        dtoObj.setLastName("(requestObj.getLastName()");
        dtoObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        dtoObj.setMobiPhone("0941667427");
        dtoObj.setId(1L);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("AUTO TEST");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(1);
        dtoObj.setStreet("requestObj.getStreet()");
        dtoObj.setAreaId(51L);
        dtoObj.setCustomerTypeId(1L);

        given( customerService.getCustomerById(any(),any())).willReturn(dtoObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.get(uri, 1)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------FindByMobiPhone------------------------------
    @Test
    public void findCustomerByMobiPhoneSuccessV1Test() throws Exception {
        String uri = V1 + root + "/phone/{phone}";

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName("requestObj.getFirstName()");
        dtoObj.setLastName("(requestObj.getLastName()");
        dtoObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        dtoObj.setMobiPhone("0941667427");
        dtoObj.setId(1L);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("AUTO TEST");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(1);
        dtoObj.setStreet("requestObj.getStreet()");
        dtoObj.setAreaId(51L);
        dtoObj.setCustomerTypeId(1L);

        List<CustomerDTO> customerDTOS = new ArrayList<>();
        customerDTOS.add(dtoObj);

        given( customerService.getCustomerByMobiPhone(any())).willReturn(customerDTOS);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.get(uri, "0941667427")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":["));
    }

    //-------------------------------FindCustomerDefault--------------------------
    @Test
    public void findCustomerDefaultSuccessV1Test() throws Exception {
        String uri = V1 + root + "/default";

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName("requestObj.getFirstName()");
        dtoObj.setLastName("(requestObj.getLastName()");
        dtoObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        dtoObj.setMobiPhone("0941667427");
        dtoObj.setId(1L);
        dtoObj.setShopId(1L);
        dtoObj.setIsDefault(true);
        dtoObj.setNameText("AUTO TEST");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(1);
        dtoObj.setStreet("requestObj.getStreet()");
        dtoObj.setAreaId(51L);
        dtoObj.setCustomerTypeId(1L);

        given( customerService.getCustomerDefault(any())).willReturn(dtoObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.get(uri)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------CreateCustomer-------------------------------
    @Test
    public void createCustomerSuccessV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("AUTO TEST");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
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
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
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
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setCreatedAt(LocalDateTime.now());
        dtoObj.setUpdatedAt(LocalDateTime.now());
        dtoObj.setShopId(1L);
        dtoObj.setNameText("First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
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
        requestObj.setStatus(1);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setCreatedAt(LocalDateTime.now());
        dtoObj.setUpdatedAt(LocalDateTime.now());
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
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
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
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
        dtoObj.setCreatedAt(LocalDateTime.now());
        dtoObj.setUpdatedAt(LocalDateTime.now());
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
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
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        requestObj.setStatus(1);
        requestObj.setAreaId(51L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setCreatedAt(LocalDateTime.now());
        dtoObj.setUpdatedAt(LocalDateTime.now());
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
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
    public void createCustomerRequiredStreetV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("First");
        requestObj.setLastName("Last");
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        requestObj.setStatus(1);
        requestObj.setMobiPhone("0941111111");
        requestObj.setAreaId(51L);
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setCreatedAt(LocalDateTime.now());
        dtoObj.setUpdatedAt(LocalDateTime.now());
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
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
        requestObj.setDob(LocalDateTime.of(2010,3,22,14,29,58));
        requestObj.setStatus(1);
        requestObj.setMobiPhone("0941111111");
        requestObj.setStreet("51L");
        requestObj.setCustomerTypeId(1L);

        CustomerDTO dtoObj = new CustomerDTO();
        dtoObj.setFirstName(requestObj.getFirstName());
        dtoObj.setLastName(requestObj.getLastName());
        dtoObj.setDob(requestObj.getDob());
        dtoObj.setMobiPhone(requestObj.getMobiPhone());
        dtoObj.setId(1L);
        dtoObj.setCreatedAt(LocalDateTime.now());
        dtoObj.setUpdatedAt(LocalDateTime.now());
        dtoObj.setShopId(1L);
        dtoObj.setNameText("Last First");
        dtoObj.setCustomerCode("CUS.SHOP1.0001");
        dtoObj.setStatus(requestObj.getStatus());
        dtoObj.setStreet(requestObj.getStreet());
        dtoObj.setAreaId(requestObj.getAreaId());
        dtoObj.setCustomerTypeId(requestObj.getCustomerTypeId());

        given( customerService.create(any(), any(), any())).willReturn(dtoObj);
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
