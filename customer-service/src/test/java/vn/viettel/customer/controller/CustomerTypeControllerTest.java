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
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.CustomerTypeService;
import vn.viettel.customer.service.impl.CustomerTypeServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerTypeControllerTest extends BaseTest{
    private final String root = "/customers/customer-types";

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @InjectMocks
    private CustomerTypeServiceImpl customerTypeService;

    @Mock
    CustomerTypeRepository repository;

    @Mock
    CustomerTypeService service;

    private List<CustomerType> customerTypeList;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        customerTypeService.setModelMapper(this.modelMapper);
        final CustomerTypeController controller = new CustomerTypeController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        customerTypeList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final CustomerType entity = new CustomerType();
            entity.setId((long) i);
            customerTypeList.add(entity);
        }
    }

    //-------------------------------GetAllCustomerType-------------------------------
   /* @Test
    public void findCustomerType() throws Exception {
        String uri = V1 + root;
        List<CustomerTypeDTO> lstDto = Arrays.asList(new CustomerTypeDTO(), new CustomerTypeDTO(), new CustomerTypeDTO());

        given(customerTypeService.getAll().willReturn(lstDto);

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }*/

//    -------------------------------findCustomerTypeByShopId-------------------------
    @Test
    public void findCustomerTypeByShopIdSuccessV1Test() throws Exception {

        Long shopId = 1L;

        String uri = V1 + root + "/shop-id/" + shopId;

        Mockito.when(repository.getWareHouseTypeIdByShopId(shopId))
                .thenReturn(customerTypeList.stream()
                        .map(customerType -> modelMapper.map(customerType, CustomerTypeDTO.class))
                        .collect(Collectors.toList())
                );


        CustomerTypeDTO customerTypeDTO = customerTypeService.getCusTypeByShopId(shopId);

        assertEquals(customerTypeList.get(0).getId(), customerTypeDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
