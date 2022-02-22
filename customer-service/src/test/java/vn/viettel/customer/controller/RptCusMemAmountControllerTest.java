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
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.entities.RptCusMemAmount;
import vn.viettel.customer.repository.RptCusMemAmountRepository;
import vn.viettel.customer.service.RptCusMemAmountService;
import vn.viettel.customer.service.impl.RptCusMemAmountServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RptCusMemAmountControllerTest extends BaseTest {

    private final String root = "/customers/prt-cus-mem-amounts";

    @InjectMocks
    private RptCusMemAmountServiceImpl rptCusMemAmountService;

    @Mock
    RptCusMemAmountRepository repository;

    @Mock
    RptCusMemAmountService service;

    private List<RptCusMemAmount> rptCusMemAmounts;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        rptCusMemAmountService.setModelMapper(this.modelMapper);
        final RptCusMemAmountController controller = new RptCusMemAmountController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        rptCusMemAmounts = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final RptCusMemAmount entity = new RptCusMemAmount();
            entity.setId((long) i);
            rptCusMemAmounts.add(entity);
        }
    }

    //-------------------------------findMemberCardById---------------------------
    @Test
    public void findRptCusMemAmountByCustomerIdSuccessV1Test() throws Exception {

        Long id = 1L;

        String uri = V1 + root + "/customer-id/" + id.toString();
        RptCusMemAmountDTO dtoObj = new RptCusMemAmountDTO();
        dtoObj.setId(1L);
        dtoObj.setCustomerTypeId(1L);
        dtoObj.setAmount(100000F);
        dtoObj.setQuantity(10);
        dtoObj.setScore(23);

        Mockito.when(repository.findByCustomerIdAndStatus(id, 1))
                .thenReturn(Optional.ofNullable(rptCusMemAmounts.get(0)));

        RptCusMemAmountDTO rptCusMemAmountDTO = rptCusMemAmountService.findByCustomerId(id);
        assertEquals(id, rptCusMemAmountDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
