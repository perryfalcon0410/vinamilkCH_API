//package vn.viettel.sale.controller;
//
//import org.junit.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import vn.viettel.sale.BaseTest;
//import vn.viettel.sale.service.WareHouseTypeService;
//import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;
//import vn.viettel.sale.service.dto.WareHouseTypeDTO;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.Assert.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class WareHouseTypeControllerTest extends BaseTest {
//    private final String root = "/sales/warehouse";
//    @MockBean
//    WareHouseTypeService wareHouseTypeService;
//
//    @Test
//    public void index() throws Exception{
//        String uri = V1 + root;
//
//        List<WareHouseTypeDTO> lists = Arrays.asList(new WareHouseTypeDTO(), new WareHouseTypeDTO());
//        given(wareHouseTypeService.index(any())).willReturn(lists);
//        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
//        String responseData = resultActions.andReturn().getResponse().getContentAsString();
//        assertThat(responseData, containsString("\"data\":["));
//    }
//}