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
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.entities.MemberCustomer;
import vn.viettel.customer.repository.MemBerCustomerRepository;
import vn.viettel.customer.service.MemberCustomerService;
import vn.viettel.customer.service.impl.MemberCustomerServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberCustomerControllerTest extends BaseTest {

    @InjectMocks
    MemberCustomerServiceImpl memberCustomerService;

    @Mock
    MemBerCustomerRepository repository;

    @Mock
    MemberCustomerService service;


    private final String root = "/customers/membercustomers";

    private List<MemberCustomer> memberCustomers;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        memberCustomerService.setModelMapper(this.modelMapper);
        final MemberCustomerController controller = new MemberCustomerController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        memberCustomers = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final MemberCustomer entity = new MemberCustomer();
            entity.setId((long) i);
            memberCustomers.add(entity);
        }
    }

    //-------------------------------CreateMemberCustomer-----------------------------
    @Test
    public void createMemberCustomerSuccessV1Test() throws Exception {
        String uri = V1 + root + "/create";
        MemberCustomerDTO requestObj = new MemberCustomerDTO();
        requestObj.setCustomerId(1l);
        requestObj.setId(1l);
        requestObj.setMemberCardId(1L);
        requestObj.setShopId(1L);

        MemberCustomer memberCustomer = memberCustomerService.create(requestObj, 1L);

        assertEquals(requestObj.getId(), memberCustomer.getId());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------findMemberCustomerById---------------------------
    @Test
    public void findMemberCustomerByIdSuccessV1Test() throws Exception {
        Long id = memberCustomers.get(0).getId();
        String uri = V1 + root + "/" + id.toString();

        MemberCustomerDTO dtoObj = new MemberCustomerDTO();
        dtoObj.setId(1L);
        dtoObj.setCustomerId(1L);
        dtoObj.setMemberCardId(1L);
        dtoObj.setShopId(1L);

        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(memberCustomers.get(0)));

        MemberCustomerDTO memberCustomerDTO = memberCustomerService.getMemberCustomerById(id);

        assertEquals(id, memberCustomerDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------findMemberCustomerByCustomerId---------------------------
    @Test
    public void findMemberCustomerByCustomerIdSuccessV1Test() throws Exception {
        Long id = memberCustomers.get(0).getId();

        String uri = V1 + root + "/" + id.toString();


        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(memberCustomers.get(0)));

        MemberCustomerDTO memberCustomerDTO = memberCustomerService.getMemberCustomerById(id);

        assertEquals(id, memberCustomerDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
