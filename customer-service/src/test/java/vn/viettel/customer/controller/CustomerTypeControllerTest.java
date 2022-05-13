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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerTypeControllerTest extends BaseTest{
    private final String root = "/customers/customer-types";

    private final String secretKey = "Feign dMV6xG4narokclMfpuJkOCFW1XYJoCsX";
    private final String headerType = "Authorization";

    @InjectMocks
    private CustomerTypeServiceImpl serviceImpl;

    @Mock
    CustomerTypeRepository repository;

    @Mock
    CustomerTypeService service;

    private List<CustomerType> customerTypeList;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImpl.setModelMapper(this.modelMapper);
        final CustomerTypeController controller = new CustomerTypeController();
        controller.setService(service);
        this.setupAction(controller);

        customerTypeList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            CustomerType entity = new CustomerType();
            entity.setPosModifyCustomer(1);
            entity.setStatus(1);
            entity.setId((long) i);
            customerTypeList.add(entity);
        }
    }

//    -------------------------------findCustomerTypeByShopId-------------------------
    @Test
    public void getCusTypeByShopId() throws Exception {

        Long shopId = 1L;

        String uri = V1 + root + "/shop-id/" + shopId;

        Mockito.when(repository.getWareHouseTypeIdByShopId(shopId))
                .thenReturn(customerTypeList.stream()
                        .map(customerType -> modelMapper.map(customerType, CustomerTypeDTO.class))
                        .collect(Collectors.toList())
                );


        CustomerTypeDTO customerTypeDTO = serviceImpl.getCusTypeByShopId(shopId);

        assertEquals(customerTypeList.get(0).getId(), customerTypeDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAllTrue() throws Exception {

        Long shopId = 1L;
        String uri = V1 + root ;

        Mockito.when(repository.findAll()).thenReturn(customerTypeList);
        List<CustomerTypeDTO> result = serviceImpl.getAll(true);
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAllFalse() throws Exception {

        Long shopId = 1L;
        String uri = V1 + root ;

        Mockito.when(repository.findAll()).thenReturn(customerTypeList);
        List<CustomerTypeDTO> result = serviceImpl.getAll(false);
        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCustomerTypeDefault() throws Exception {

        String uri = V1 + root + "/default" ;

        List<CustomerTypeDTO> customerTypes = Arrays.asList(new CustomerTypeDTO());
        Mockito.when(repository.getCustomerTypeDefault()).thenReturn(customerTypes);
        serviceImpl.getCustomerTypeDefaut();

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getWarehouseTypeByShopId() throws Exception {
        Long shopId = 1L;
        String uri = V1 + root + "/warehouse-type/shop/" + shopId.toString() ;

        List<CustomerTypeDTO> customerTypes = Arrays.asList(new CustomerTypeDTO());
        Mockito.when(repository.getWareHouseTypeIdByShopId(shopId)).thenReturn(null);
        Mockito.when(repository.getCustomerTypeDefault()).thenReturn(customerTypes);
        serviceImpl.getWarehouseTypeByShopId(shopId);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCusTypeById() throws Exception {
        Long id = 1L;
        String uri = V1 + root + "/" + id.toString() ;

        List<CustomerType> customerTypes = Arrays.asList(new CustomerType());
        Mockito.when(repository.findByIds(Arrays.asList(id))).thenReturn(customerTypes);
        serviceImpl.findByIds(Arrays.asList(id));

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCusTypeByWarehouse() throws Exception {
        Long warehouseId = 1L;
        String uri = V1 + root + "/getbywarehouse"  ;

        List<CustomerType> customerTypes = Arrays.asList(new CustomerType());
        Mockito.when(repository.getByWarehouse(warehouseId)).thenReturn(customerTypes);
        serviceImpl.findByWarehouse(warehouseId);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCustomerTypeForSale() throws Exception {
        Long customerId = 1L;
        Long shopId = 1L;
        String uri = V1 + root + "/sale-order"  ;

        List<CustomerTypeDTO> customerTypes = Arrays.asList(new CustomerTypeDTO());
        Mockito.when(repository.getByCustomerId(customerId)).thenReturn(customerTypes);
        serviceImpl.getCustomerType(customerId, shopId);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAll1() throws Exception {
        Long customerId = 1L;
        Long shopId = 1L;
        String uri = V1 + root ;
        Boolean isCreate = false;

        CustomerType customerType = new CustomerType();
        customerType.setStatus(1);
        List<CustomerType> customerTypes = Arrays.asList(customerType);
        Mockito.when(repository.findAll()).thenReturn(customerTypes);
        serviceImpl.getAll(isCreate);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getAll2() throws Exception {
        Long customerId = 1L;
        Long shopId = 1L;
        String uri = V1 + root ;
        Boolean isCreate = true;

        CustomerType customerType = new CustomerType();
        customerType.setStatus(1);
        customerType.setPosModifyCustomer(1);
        List<CustomerType> customerTypes = Arrays.asList(customerType);
        Mockito.when(repository.findAll()).thenReturn(customerTypes);
        serviceImpl.getAll(isCreate);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
