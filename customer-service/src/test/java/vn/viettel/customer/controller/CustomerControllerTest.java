package vn.viettel.customer.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.customer.entities.MemberCustomer;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.repository.MemBerCustomerRepository;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.feign.ApParamClient;
import vn.viettel.customer.service.feign.AreaClient;
import vn.viettel.customer.service.feign.CategoryDataClient;
import vn.viettel.customer.service.feign.ShopClient;
import vn.viettel.customer.service.impl.CustomerServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerTest extends BaseTest {

    private final String root = "/customers";

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    CustomerRepository repository;

    @Mock
    CustomerService service;

    @Mock
    CategoryDataClient categoryDataClient;

    @Mock
    ShopClient shopClient;

    @Mock
    AreaClient areaClient;

    @Mock
    MemBerCustomerRepository memBerCustomerRepository;

    @Mock
    CustomerTypeRepository customerTypeRepository;

    @Mock
    ApParamClient apParamClient;

    private List<Customer> customerList;

    private List<CategoryDataDTO> categoryDataDTOS;

    private List<ApParamDTO> apParamDTOS;

    private List<AreaDTO> areaDTOS;

    private List<MemberCustomer> memberCustomers;

    private List<CustomerType> customerTypes;

    private List<ShopDTO> shopDTOS;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        customerService.setModelMapper(this.modelMapper);
        final CustomerController controller = new CustomerController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        customerList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final Customer entity = new Customer();
            entity.setId((long) i);
            entity.setAddress("Address" + i);
            entity.setEmail("email." + i + "@email.com");
            entity.setPhone("0941667427");
            entity.setFirstName("A");
            entity.setLastName("B");
            entity.setNameText("A B");
            entity.setShopId((long) i);
            entity.setCustomerTypeId(1L);
            entity.setCustomerCode("ABCDDNENE1234" + i);
            entity.setDob(LocalDateTime.of(1993,10,10,10,10));
            customerList.add(entity);
        }

        categoryDataDTOS = new ArrayList<>();

        apParamDTOS = new ArrayList<>();
        final ApParamDTO apParamDTO = new ApParamDTO();
        apParamDTO.setStatus(1);
        apParamDTO.setApParamName("18");
        apParamDTOS.add(apParamDTO);

        areaDTOS = new ArrayList<>();
        final AreaDTO areaDTO = new AreaDTO();
        areaDTO.setId(1L);
        areaDTO.setAreaName("A");
        areaDTO.setAreaCode("A");
        areaDTO.setDistrict("A");
        areaDTOS.add(areaDTO);

        memberCustomers = new ArrayList<>();
        final MemberCustomer memberCustomer = new MemberCustomer();
        memberCustomer.setId(1L);
        memberCustomer.setShopId(1L);
        memberCustomers.add(memberCustomer);

        customerTypes = new ArrayList<>();
        final CustomerType customerType = new CustomerType();
        customerType.setId(1L);
        customerType.setStatus(1);
        customerType.setPosModifyCustomer(1);
        customerTypes.add(customerType);

        shopDTOS = new ArrayList<>();
        final ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTOS.add(shopDTO);
    }

    //-------------------------------GetAllCustomer-------------------------------
    @Test
    public void getAllCustomer() throws Exception {
        String uri = V1 + root;

        CustomerFilter customerFilter = new CustomerFilter();

        int size = 5;
        int page = 1;

        PageRequest pageReq = PageRequest.of(page, size);

        Page<Customer> pageCustomer = new PageImpl<>(customerList, pageReq, customerList.size());

        Response<List<CategoryDataDTO>> response = new Response<>();
        response.withData(categoryDataDTOS);
        Mockito.when(categoryDataClient.getGendersV1()).thenReturn(response);

        Mockito.when(repository.findAllBy(
                null,
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                pageReq
        )).thenReturn(pageCustomer);

        Page<CustomerDTO> customerDTOPage = customerService.index(customerFilter, pageReq);

        assertEquals(customerList.size(), customerDTOPage.getSize());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------UpdateCustomer-------------------------------
    @Test
    public void updateCustomerSuccessV1Test() throws Exception {

        Long id = customerList.get(0).getId();

        String uri = V1 + root + "/update/" + id.toString();

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setId(1L);
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0982222428");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);
        requestObj.setCustomerCode("CUS.SHOP1.0001");


        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(customerList.get(0)));

        Mockito.when(shopClient.getLevelUpdateCustomerV1(1L)).thenReturn(new Response<Long>().withData(1L));

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        CustomerDTO customerDTO = customerService.update(requestObj, id, 1L, true);

        assertEquals(requestObj.getId(), customerDTO.getId());

        ResultActions resultActions = mockMvc.perform(patch(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------FindById-------------------------------------
    @Test
    public void findCustomerByIdSuccessV1Test() throws Exception {
        Long id = customerList.get(0).getId();
        String uri = V1 + root + "/" + id.toString();

        Mockito.when(repository.getById(id)).thenReturn(customerList.get(0));

        Mockito.when(memBerCustomerRepository.getMemberCustomer(1L))
                .thenReturn(Optional.ofNullable(memberCustomers.get(0)));

        Mockito.when(shopClient.getLevelUpdateCustomerV1(1L))
                .thenReturn(new Response<Long>().withData(1L));

        Mockito.when(customerTypeRepository.getById(1L))
                .thenReturn(customerTypes.get(0));

        CustomerDTO customerDTO = customerService.getCustomerById(id, 1L);

        assertEquals(id, customerDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------FindByMobiPhone------------------------------
    @Test
    public void findCustomerByMobiPhoneSuccessV1Test() throws Exception {
        String phone = "0941667427";
        String uri = V1 + root + "/phone/" + phone;

        Mockito.when(repository.getAllByMobiPhoneAndStatus(phone,1)).thenReturn(customerList);

        List<CustomerDTO> customerDTOS = customerService.getCustomerByMobiPhone(phone);

        assertEquals(customerList.size(), customerDTOS.size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------FindCustomerDefault--------------------------
    @Test
    public void findCustomerDefaultSuccessV1Test() throws Exception {

        String uri = V1 + root + "/default";

        Mockito.when(repository.getCustomerDefault(1L))
                .thenReturn(customerList.stream()
                        .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                        .collect(Collectors.toList())
                );

        CustomerDTO customerDTO = customerService.getCustomerDefault(1L);

        assertEquals(customerList.get(0).getId(), customerDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------CreateCustomer-------------------------------
    @Test
    public void createCustomerSuccessV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createCustomerRequiredFirstNameV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createCustomerRequiredLastNameV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createCustomerRequiredDOBV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createCustomerRequiredStatusV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createCustomerRequiredMobiPhoneV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createCustomerRequiredStreetV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void createCustomerRequiredAreaV1Test() throws Exception {
        String uri = V1 + root + "/create";

        CustomerRequest requestObj = new CustomerRequest();
        requestObj.setFirstName("Test");
        requestObj.setLastName("Auto");
        requestObj.setDob(LocalDateTime.of(1993,3,22,14,29,58));
        requestObj.setMobiPhone("0941667427");
        requestObj.setStatus(1);
        requestObj.setAreaId(1L);
        requestObj.setStreet("123");
        requestObj.setCustomerTypeId(1L);

        Mockito.when(apParamClient.getApParamByCodeV1("MIN_AGE"))
                .thenReturn(new Response<ApParamDTO>().withData(apParamDTOS.get(0)));

        Mockito.when(areaClient.getByIdV1(1L)).thenReturn(new Response<AreaDTO>().withData(areaDTOS.get(0)));

        Mockito.when(shopClient.getShopByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shopDTOS.get(0)));

        Mockito.when(repository.getLastCustomerNumber(1L,PageRequest.of(0,1)))
                .thenReturn( new PageImpl<>(customerList, PageRequest.of(0,1), customerList.size()));

        Mockito.when(repository.save(any())).thenReturn(customerList.get(0));

        CustomerDTO customerDTO = customerService.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
