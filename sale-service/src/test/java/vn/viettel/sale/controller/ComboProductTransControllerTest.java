package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.entities.ComboProductTrans;
import vn.viettel.sale.messaging.ComboProductTranDetailRequest;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.repository.ComboProductRepository;
import vn.viettel.sale.repository.ComboProductTransRepository;
import vn.viettel.sale.service.ComboProductService;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.TotalDTO;
import vn.viettel.sale.service.impl.ComboProductServiceImpl;
import vn.viettel.sale.service.impl.ComboProductTransServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ComboProductTransControllerTest extends BaseTest {
    private final String root = "/sales/combo-product-trans";
    private final String uri = V1 + root;

    @InjectMocks
    ComboProductTransServiceImpl serviceImp;

    @Mock
    ComboProductTransService service;

    @Mock
    ComboProductTransRepository repository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ComboProductTransController controller = new ComboProductTransController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    //-------------------------------findComboProductTrans-------------------------------
    @Test
    public void findComboProductTransTest() throws Exception {
        CoverResponse<Page<ComboProductTranDTO>, TotalDTO> data = new CoverResponse<>();
        List<ComboProductTranDTO> listData = Arrays.asList(new ComboProductTranDTO());
        data.setResponse(new PageImpl<>(listData));
        data.setInfo(new TotalDTO());

//        List<ComboProductTrans> tasks = new ArrayList<>();
//        Page<ComboProductTrans> pagedTasks = new PageImpl(tasks);
//        Mockito.when(this.repository.findAll(Mockito.any(PageRequest.class))).thenReturn(pagedTasks);
        Pageable page = PageRequest.of(1, 10);
        CoverResponse<Page<ComboProductTranDTO>, TotalDTO>  result = service.getAll(new ComboProductTranFilter(), page);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------create-------------------------------
    @Test
    public void createTest() throws Exception {
        ComboProductTranRequest data = new ComboProductTranRequest();
        data.setTransDate(LocalDateTime.now());
        data.setTransType(1);

        ComboProductTranDetailRequest combo = new ComboProductTranDetailRequest();
        combo.setComboProductId(1L);
        combo.setPrice(10000.0);
        combo.setQuantity(10);
        data.setDetails(Arrays.asList(combo));

        ComboProductTranDTO response = service.create(any(), any(), any());

        String inputJson = super.mapToJson(data);
        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getComboProductTran-------------------------------
    @Test
    public void getComboProductTranTest() throws Exception {
        String url = uri + "/{id}";

        ComboProductTranDTO result = new ComboProductTranDTO();

        given(service.getComboProductTrans(any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
