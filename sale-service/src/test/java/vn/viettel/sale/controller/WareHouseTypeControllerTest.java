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
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.WareHouseTypeService;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;
import vn.viettel.sale.service.feign.CustomerTypeClient;
import vn.viettel.sale.service.impl.WareHouseTypeServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WareHouseTypeControllerTest extends BaseTest {
    private final String root = "/sales/warehouse";
    @InjectMocks
    WareHouseTypeServiceImpl serviceImp;

    @Mock
    WareHouseTypeService service;

    @Mock
    WareHouseTypeRepository repository;

    @Mock
    CustomerTypeClient customerTypeClient;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final WareHouseTypeController controller = new WareHouseTypeController();
        controller.setService(service);
        this.setupAction(controller);
    }

    @Test
    public void index() throws Exception{
        String uri = V1 + root;
        Long shopId = 1L;
        Long wareHouseId = 2L;
        given(customerTypeClient.getWarehouseTypeByShopId(shopId)).willReturn(wareHouseId);
        List<WareHouseTypeDTO> lists = Arrays.asList(new WareHouseTypeDTO(), new WareHouseTypeDTO());
        given(repository.findWithDefault(wareHouseId)).willReturn(lists);
        List<WareHouseTypeDTO> result = serviceImp.index(shopId);

        assertEquals(lists.size(), result.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}