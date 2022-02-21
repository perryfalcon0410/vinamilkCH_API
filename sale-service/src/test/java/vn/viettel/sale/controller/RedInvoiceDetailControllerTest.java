package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.sale.repository.RedInvoiceDetailRepository;
import vn.viettel.sale.service.RedInvoiceDetailService;
import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;
import vn.viettel.sale.service.impl.RedInvoiceDetailServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RedInvoiceDetailControllerTest extends BaseTest {

    private final String root = "/sales";

    @InjectMocks
    RedInvoiceDetailServiceImpl serviceImp;

    @Mock
    RedInvoiceDetailService service;

    @Mock
    RedInvoiceDetailRepository repository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final RedInvoiceDetailController controller = new RedInvoiceDetailController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getRedInvoiceDetailByRedInvoiceId() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/red-invoice-detail/" + id.toString();
        List<RedInvoiceDetail> lists = Arrays.asList(new RedInvoiceDetail(), new RedInvoiceDetail());
        given(repository.getAllByRedInvoiceId(id)).willReturn(lists);
        List<RedInvoiceDetailDTO> result = serviceImp.getRedInvoiceDetailByRedInvoiceId(id);

        assertEquals(lists.size(), result.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

}
