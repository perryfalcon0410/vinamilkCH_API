package vn.viettel.common.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.common.BaseTest;
import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.service.AreaService;
import vn.viettel.common.service.dto.AreaDefaultDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AreaControllerTest extends BaseTest {
    private final String root = "/commons/areas";

    @MockBean
    AreaService areaService;

    @Test
    public void getProvinces() throws Exception{
        String uri = V1 + root + "/provinces";
        List<AreaDTO> apParamDTOS = Arrays.asList(new AreaDTO() , new AreaDTO());
        given(areaService.getProvinces())
                .willReturn(apParamDTOS);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));


    }

    @Test
    public void getAreaDefault() throws Exception{
        String uri = V1 + root + "/default";
        given(areaService.getAreaDefault(any()))
                .willReturn(new AreaDefaultDTO());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void getDistrictsToSearchCustomer() throws Exception{
        String uri = V1 + root + "/districts/index-customers";
        List<AreaSearch> areaSearches = Arrays.asList(new AreaSearch() , new AreaSearch());
        given(areaService.getDistrictsToSearchCustomer(any()))
                .willReturn(areaSearches);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void getDistrictsByProvinceId() throws Exception{
        String uri = V1 + root + "/districts";
        List<AreaDTO> areaSearches = Arrays.asList(new AreaDTO() , new AreaDTO());
        given(areaService.getDistrictsByProvinceId(any()))
                .willReturn(areaSearches);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("provinceId" , "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void getPrecinctsByDistrictId() throws Exception{
        String uri = V1 + root + "/precincts";
        List<AreaDTO> areaSearches = Arrays.asList(new AreaDTO() , new AreaDTO());
        given(areaService.getPrecinctsByDistrictId(any()))
                .willReturn(areaSearches);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("districtId" , "3" )
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getById() throws Exception{
        String uri = V1 + root + "/5";
        given(areaService.getAreaById(any()))
                .willReturn(new AreaDTO());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    @Test
    public void getArea() throws Exception{
        String uri = V1 + root + "/find";
        given(areaService.getArea(any(),any(),any()))
                .willReturn(new AreaDTO());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
//        resultActions.andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }
}