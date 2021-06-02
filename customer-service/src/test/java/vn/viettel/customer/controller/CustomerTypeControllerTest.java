//package vn.viettel.customer.controller;
//
//import org.junit.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import vn.viettel.core.dto.customer.CustomerTypeDTO;
//import vn.viettel.customer.BaseTest;
//import vn.viettel.customer.service.CustomerTypeService;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class CustomerTypeControllerTest extends BaseTest{
//    private final String root = "/customers/customer-types";
//
//    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
//    private final String headerType = "Authorization";
//
//    @MockBean
//    private CustomerTypeService customerTypeService;
//
//    //-------------------------------GetAllCustomerType-------------------------------
//    @Test
//    public void findCustomerType() throws Exception {
//        String uri = V1 + root;
//        List<CustomerTypeDTO> lstDto = Arrays.asList(new CustomerTypeDTO(), new CustomerTypeDTO(), new CustomerTypeDTO());
//
//        given(customerTypeService.getAll()).willReturn(lstDto);
//
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//    }
//
//    //-------------------------------findCustomerTypeByShopId-------------------------
//    @Test
//    public void findCustomerTypeByShopIdSuccessV1Test() throws Exception {
//        String uri = V1 + root + "/shop-id/{shopId}";
//
//        CustomerTypeDTO dtoObj = new CustomerTypeDTO();
//        dtoObj.setId(1L);
//        dtoObj.setStatus(1);
//        dtoObj.setCode("123");
//        dtoObj.setName("Khách Hàng Thân Thiết DLĐộng");
//
//
//        given( customerTypeService.getCusTypeByShopId(anyLong())).willReturn(dtoObj);
//        ResultActions resultActions =  mockMvc
//                .perform(MockMvcRequestBuilders.get(uri, 1)
//                        .header(headerType, secretKey))
//                .andDo(MockMvcResultHandlers.print());
//        MvcResult mvcResult = resultActions.andReturn();
//        assertEquals(200, mvcResult.getResponse().getStatus());
//    }
//
//}
