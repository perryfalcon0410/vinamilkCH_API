package vn.viettel.common.controller;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.common.BaseTest;
import vn.viettel.common.service.CategoryDataService;
import vn.viettel.core.dto.common.CategoryDataDTO;

import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
public class CategoryDataControllerTest extends BaseTest {
    private final String root = "/commons/categorydata";

    @MockBean
    private CategoryDataService categoryDataService;

    @Test
    public void getGenders() throws Exception{

        String uri = V1 + root + "/genders";

        List<CategoryDataDTO> lstDto = Arrays.asList(new CategoryDataDTO(), new CategoryDataDTO());

        given(categoryDataService.getGenders()).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void getReasonExchange() throws Exception {

        String uri = V1 + root + "/get-reason-exchange";

        List<CategoryDataDTO> lstDto = Arrays.asList(new CategoryDataDTO(), new CategoryDataDTO());

        given(categoryDataService.getListReasonExchange()).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getCategoryDataById() throws Exception {
        Long id = 4L;
        String uri = V1 + root + "/" + id;


        CategoryDataDTO lstDto = new CategoryDataDTO();

        given(categoryDataService.getCategoryDataById(id)).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getByCategoryGroupCode() throws Exception {

        String uri = V1 + root + "/get-by-group-code";

        List<CategoryDataDTO> lstDto = Arrays.asList(new CategoryDataDTO(), new CategoryDataDTO());

        given(categoryDataService.getListReasonExchange()).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getReasonById() throws Exception {
        Long id = 4L;
        String uri = V1 + root + "/reason/" + id;


        CategoryDataDTO lstDto = new CategoryDataDTO();

        given(categoryDataService.getCategoryDataById(id)).willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//        resultActions.andDo(MockMvcResultHandlers.print());
    }

}