package vn.viettel.customer.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.entities.MemberCard;
import vn.viettel.customer.repository.MemberCardRepository;
import vn.viettel.customer.service.MemberCardService;
import vn.viettel.customer.service.impl.MemberCardServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberCardControllerTest extends BaseTest {
    private final String root = "/customers/membercards";

    @InjectMocks
    private MemberCardServiceImpl memberCardService;

    @Mock
    MemberCardRepository repository;

    @Mock
    MemberCardService service;

    private List<MemberCard> memberCardList;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        memberCardService.setModelMapper(this.modelMapper);
        final MemberCardController controller = new MemberCardController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        memberCardList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final MemberCard entity = new MemberCard();
            entity.setId((long) i);
            memberCardList.add(entity);
        }
    }

    //-------------------------------createMemberCard-----------------------------
    @Test
    public void createMemberCardSuccessV1Test() throws Exception {
        String uri = V1 + root + "/create";
        MemberCardDTO requestObj = new MemberCardDTO();
        requestObj.setId(1L);
        requestObj.setMemberCardCode("123");
        requestObj.setMemberCardName("card1");
        requestObj.setStatus(1);

        MemberCard memberCard = memberCardService.create(requestObj, 1L);

        assertEquals(requestObj.getId(), memberCard.getId());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------findMemberCardById---------------------------
    @Test
    public void findMemberCardByIdSuccessV1Test() throws Exception {
        Long id = memberCardList.get(0).getId();
        String uri = V1 + root + "/" + id.toString();

        Mockito.when(repository.getMemberCardById(id)).thenReturn(Optional.ofNullable(memberCardList.get(0)));

        MemberCardDTO memberCardDTO = memberCardService.getMemberCardById(id);

        assertEquals(id, memberCardDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------createMemberCard-----------------------------
    @Test
    public void updateMemberCardSuccessV1Test() throws Exception {
        Long id = memberCardList.get(0).getId();
        String uri = V1 + root + "/update/" + id.toString();
        MemberCardDTO requestObj = new MemberCardDTO();
        requestObj.setId(1L);
        requestObj.setMemberCardCode("123");
        requestObj.setMemberCardName("card1");
        requestObj.setStatus(1);

        Mockito.when(repository.getMemberCardById(id)).thenReturn(Optional.ofNullable(memberCardList.get(0)));

        MemberCard memberCard = memberCardService.update(requestObj);

        assertEquals(requestObj.getId(), memberCard.getId());

        ResultActions resultActions = mockMvc.perform(put(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

}
