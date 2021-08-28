package vn.viettel.customer.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.entities.MemberCard;
import vn.viettel.customer.service.MemberCardService;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class MemberCardControllerTest extends BaseTest {
    private final String root = "/customers/membercards";

    @MockBean
    private MemberCardService memberCardService;

    //-------------------------------createMemberCard-----------------------------
    @Test
    public void createMemberCardSuccessV1Test() throws Exception {
        String uri = V1 + root + "/create";
        MemberCardDTO requestObj = new MemberCardDTO();
        requestObj.setId(1L);
        requestObj.setMemberCardCode("123");
        requestObj.setMemberCardName("card1");
        requestObj.setStatus(1);

        MemberCard dtoObj = modelMapper.map(requestObj, MemberCard.class);

        given( memberCardService.create(any(), any())).willReturn(dtoObj);
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

    //-------------------------------findMemberCardById---------------------------
    @Test
    public void findMemberCardByIdSuccessV1Test() throws Exception {
        String uri = V1 + root + "/{id}";

        MemberCardDTO dtoObj = new MemberCardDTO();
        dtoObj.setId(1L);
        dtoObj.setMemberCardCode("123");
        dtoObj.setMemberCardName("card1");
        dtoObj.setStatus(1);

        given( memberCardService.getMemberCardById(any())).willReturn(dtoObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.get(uri, 1)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

    //-------------------------------createMemberCard-----------------------------
    @Test
    public void updateMemberCardSuccessV1Test() throws Exception {
        String uri = V1 + root + "/update/{id}";
        MemberCardDTO requestObj = new MemberCardDTO();
        requestObj.setId(1L);
        requestObj.setMemberCardCode("123");
        requestObj.setMemberCardName("card1");
        requestObj.setStatus(1);

        MemberCard dtoObj = modelMapper.map(requestObj, MemberCard.class);

        given( memberCardService.update(any())).willReturn(dtoObj);
        String inputJson = super.mapToJson(requestObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.put(uri,1)
                        .content(inputJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }

}
