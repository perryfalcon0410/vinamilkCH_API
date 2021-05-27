package vn.viettel.common.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.common.BaseTest;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.dto.common.ApParamDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApParamControllerTest extends BaseTest {
    private final String root = "/commons/apparams";

    @MockBean
    ApParamService apParamService;

    @Test
    public void getApParamById() throws Exception{
        String uri = V1 + root + "/5";

        given(apParamService.getApParamById(any()))
                .willReturn(new ApParamDTO());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getCardTypes() throws Exception{
        String uri = V1 + root + "/cardtypes";
        List<ApParamDTO> apParamDTOS = Arrays.asList(new ApParamDTO() , new ApParamDTO());
        given(apParamService.getCardTypes())
                .willReturn(apParamDTOS);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getCloselytypes() throws Exception{
        String uri = V1 + root + "/closelytypes";
        List<ApParamDTO> apParamDTOS = Arrays.asList(new ApParamDTO() , new ApParamDTO());
        given(apParamService.getCloselytypes())
                .willReturn(apParamDTOS);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getReasonAdjust() throws Exception{
        String uri = V1 + root + "/reason-adjust/5";
        given(apParamService.getReason(any()))
                .willReturn(new ApParamDTO());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getByType() throws Exception{
        String uri = V1 + root + "/type/SALEMT_CLOSELY_CUSTOMER";
        List<ApParamDTO> apParamDTOS = Arrays.asList(new ApParamDTO() , new ApParamDTO());
        given(apParamService.getByType(any()))
                .willReturn(apParamDTOS);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getReasonNotImport() throws Exception{
        String uri = V1 + root + "/sale-mt-deny";
        List<ApParamDTO> apParamDTOS = Arrays.asList(new ApParamDTO() , new ApParamDTO());
        given(apParamService.getReasonNotImport())
                .willReturn(apParamDTOS);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getApParams() throws Exception{
        String uri = V1 + root;

        List<ApParamDTO> apParamDTOS = Arrays.asList(new ApParamDTO() , new ApParamDTO());
        given(apParamService.findAll())
                .willReturn(apParamDTOS);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getApParamByCode() throws Exception{
        String uri = V1 + root + "/getByCode/1";
        given(apParamService.getByCode(any()))
                .willReturn(new ApParamDTO());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
    }
}