//package vn.viettel.customer.controller;
//
//import org.junit.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import vn.viettel.core.dto.customer.MemberCardDTO;
//import vn.viettel.core.dto.customer.MemberCustomerDTO;
//import vn.viettel.customer.BaseTest;
//import vn.viettel.customer.entities.MemberCustomer;
//import vn.viettel.customer.service.MemberCustomerService;
//
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//
//public class MemberCustomerControllerTest extends BaseTest {
//
//    @MockBean
//    MemberCustomerService memberCustomerService;
//
//    private final String root = "/customers/membercustomers";
//
//    //-------------------------------CreateMemberCustomer-----------------------------
//    @Test
//    public void createMemberCustomerSuccessV1Test() throws Exception {
//        String uri = V1 + root + "/create";
//        MemberCustomerDTO requestObj = new MemberCustomerDTO();
//        requestObj.setCustomerId(1l);
//        requestObj.setId(1l);
//        requestObj.setMemberCardId(1L);
//        requestObj.setShopId(1L);
//
//        MemberCustomer dtoObj = modelMapper.map(requestObj, MemberCustomer.class);
//        given( memberCustomerService.create(any(), any())).willReturn(dtoObj);
//        String inputJson = super.mapToJson(requestObj);
//        ResultActions resultActions =  mockMvc
//                .perform(MockMvcRequestBuilders.post(uri)
//                        .content(inputJson)
//                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
//
//    //-------------------------------findMemberCustomerById---------------------------
//    @Test
//    public void findMemberCustomerByIdSuccessV1Test() throws Exception {
//        String uri = V1 + root + "/{id}";
//
//        MemberCustomerDTO dtoObj = new MemberCustomerDTO();
//        dtoObj.setId(1L);
//        dtoObj.setCustomerId(1L);
//        dtoObj.setMemberCardId(1L);
//        dtoObj.setShopId(1L);
//
//        given( memberCustomerService.getMemberCustomerById(any())).willReturn(dtoObj);
//        ResultActions resultActions =  mockMvc
//                .perform(MockMvcRequestBuilders.get(uri, 1)
//                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
//
//    //-------------------------------findMemberCustomerByCustomerId---------------------------
//    @Test
//    public void findMemberCustomerByCustomerIdSuccessV1Test() throws Exception {
//        String uri = V1 + root + "/{id}";
//
//        MemberCustomerDTO dtoObj = new MemberCustomerDTO();
//        dtoObj.setId(1L);
//        dtoObj.setCustomerId(1L);
//        dtoObj.setMemberCardId(1L);
//        dtoObj.setShopId(1L);
//
//        given( memberCustomerService.getMemberCustomerById(any())).willReturn(dtoObj);
//        ResultActions resultActions =  mockMvc
//                .perform(MockMvcRequestBuilders.get(uri, 1)
//                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
//    }
//}
