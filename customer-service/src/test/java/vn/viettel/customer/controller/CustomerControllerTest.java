package vn.viettel.customer.controller;

import io.swagger.annotations.ApiParam;
import oracle.net.aso.p;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.SortDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.CustomerOnlRequest;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.customer.BaseTest;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.customer.entities.MemberCustomer;
import vn.viettel.customer.messaging.CusRedInvoiceFilter;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.messaging.CustomerSaleFilter;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.repository.MemBerCustomerRepository;
import vn.viettel.customer.repository.RptCusMemAmountRepository;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.CustomerTypeService;
import vn.viettel.customer.service.MemberCardService;
import vn.viettel.customer.service.MemberCustomerService;
import vn.viettel.customer.service.feign.*;
import vn.viettel.customer.service.impl.CustomerServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerTest extends BaseTest {

    private final String root = "/customers";

    @InjectMocks
    private CustomerServiceImpl serviceImpl;

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

    @Mock
    UserClient userClient;

    @Mock
    MemberCardService memberCardService;

    @Mock
    MemberCustomerService memberCustomerService;

    @Mock
    RptCusMemAmountRepository rptCusMemAmountRepository;

    @Mock
    CustomerTypeService customerTypeService;

    @Mock
    MemBerCustomerRepository memBerCustomerRepos;

    @Mock
    public EntityManager entityManager;

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
        serviceImpl.setModelMapper(this.modelMapper);
        final CustomerController controller = new CustomerController();
        controller.setService(service);
        this.setupAction(controller);

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

        Page<CustomerDTO> customerDTOPage = serviceImpl.index(customerFilter, pageReq);

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

        CustomerDTO customerDTO = serviceImpl.update(requestObj, id, 1L, true);

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

        Mockito.when(shopClient.getLevelUpdateCustomerV1(1L))
                .thenReturn(new Response<Long>().withData(1L));

        Mockito.when(customerTypeRepository.getById(1L))
                .thenReturn(customerTypes.get(0));

        CustomerDTO customerDTO = serviceImpl.getCustomerById(id, 1L);

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

        List<CustomerDTO> customerDTOS = serviceImpl.getCustomerByMobiPhone(phone);

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

        CustomerDTO customerDTO = serviceImpl.getCustomerDefault(1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

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

        CustomerDTO customerDTO = serviceImpl.create(requestObj, 1L, 1L);

        assertEquals(requestObj.getMobiPhone(), customerDTO.getPhone());

        ResultActions resultActions = mockMvc.perform(post(uri)
                        .content(super.mapToJson(requestObj))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCustomerForAutoComplete() throws Exception {
        String uri = V1 + root  + "/customers-to-sale";
        Long shopId = 1L;
        String searchKeywords = "1234";
        String customerPhone = "456987";
        List<Long> customers = Arrays.asList(1L);
        Pageable pageable = PageRequest.of(0,5);
        String name = VNCharacterUtils.removeAccent(searchKeywords).toUpperCase();
        List<CustomerDTO> lstCustomerDTO = new ArrayList<>();
        CustomerDTO customerDTO = new CustomerDTO();
        lstCustomerDTO.add(customerDTO);
        Page<CustomerDTO> page = new PageImpl<>(lstCustomerDTO, pageable, lstCustomerDTO.size());
        Mockito.when(repository.searchCustomer(name, searchKeywords.toUpperCase(), searchKeywords, shopId, pageable))
                .thenReturn(page);

        serviceImpl.getCustomerForAutoComplete(searchKeywords, shopId, pageable);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void create() {
    }

    @Test
    public void createForFeign() throws Exception {
        String uri = V1 + root  + "/feign";
        Long shopId = 1L;
        CustomerOnlRequest request = new CustomerOnlRequest();
        request.setLastName("LastName");
        request.setFirstName("FirstName");
        request.setAddress("Address");
        ShopDTO shop = shopDTOS.get(0);
        shop.setShopCode("123");
        Mockito.when(shopClient.getShopByIdV1(shopId))
                .thenReturn(new Response<ShopDTO>().withData(shop));
        PageRequest pageReq = PageRequest.of(0,1);

        Page<Customer> pageCustomer = new PageImpl<>(customerList, pageReq, customerList.size());
        Mockito.when(repository.getLastCustomerNumber(shopId, pageReq))
                .thenReturn(pageCustomer);
        Mockito.when(customerTypeService.getCustomerTypeDefaut())
                .thenReturn(new CustomerTypeDTO());

        serviceImpl.createForOnlOrder(request, shopId);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(request))
                .param("shopId", shopId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCustomerById() {
    }

    @Test
    public void getCustomerByMobiPhone() {
    }

    @Test
    public void update() {
    }

    @Test
    public void excelCustomersReport() throws Exception {
        String uri = V1 + root  + "/export";
        String searchKeywords = "123456";
        Long customerTypeId = 1L;
        Integer status = 1;
        Boolean isShop = true;
        Long genderId = 1L;
        Long areaId = 1L;
        String phone = "0982888758";
        String idNo = "147896325";
        Long id = 1L;
        CustomerFilter filter = new CustomerFilter(searchKeywords, customerTypeId, status, genderId, areaId, phone, idNo, id,isShop);
        Pageable pageable = PageRequest.of(0,5);
        AreaDTO areaDTO = new AreaDTO();
        List<AreaDTO> precincts = Arrays.asList(areaDTO);
        Response response = new Response();
        response.withData(precincts);
        Mockito.when(areaClient.getPrecinctsByDistrictIdV1(filter.getAreaId()))
                .thenReturn(response);

        Customer customer = new Customer();
        customer.setCustomerTypeId(1L);
        customer.setStatus(1);
        List<Customer> customers = Arrays.asList(customer);
        Mockito.when(repository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(customers);

        CustomerTypeDTO customerTypeDTO = new CustomerTypeDTO();
        List<CustomerTypeDTO> customerTypes = Arrays.asList(customerTypeDTO);
        Mockito.when(customerTypeService.findByIds(Arrays.asList(1L)))
                .thenReturn(customerTypes);

        Response response1 = new Response<ApParamDTO>();
        response1.withData(apParamDTOS);
        Mockito.when(apParamClient.getCloselytypesV1()).thenReturn(response1);

        Mockito.when(apParamClient.getCardTypesV1()).thenReturn(response1);

        Response response2 = new Response<CategoryDataDTO>();
        response2.withData(categoryDataDTOS);
        Mockito.when(categoryDataClient.getGendersV1()).thenReturn(response2);

        serviceImpl.exportExcel(filter);

        ResultActions resultActions = mockMvc
                .perform(put(uri).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void updateFeign() throws Exception {
        String uri = V1 + root  + "/feign/update/{id}";
        Long id = 1L;
        CustomerRequest request = new CustomerRequest();
        request.setId(1L);
        Pageable pageable = PageRequest.of(0,5);
        Customer customerDb = new Customer();
        Mockito.when(repository.findById(request.getId()))
                .thenReturn(Optional.of(customerDb));

        serviceImpl.updateForSale(request, id);

        ResultActions resultActions = mockMvc
                .perform(put(uri, id).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getIdCustomerBySearchKeyWords() throws Exception {
        String uri = V1 + root  + "/ids-customer-by-keyword";
        Long shopId = 1L;
        String searchKeywords = "123";
        String customerPhone = "456987";
        List<Long> customers = Arrays.asList(1L);
        Pageable pageable = PageRequest.of(0,5);
        Mockito.when(repository.getCustomersIds(searchKeywords, searchKeywords.toUpperCase(Locale.ROOT), searchKeywords))
                .thenReturn(customers);

        serviceImpl.getIdCustomerBySearchKeyWords(searchKeywords);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getIdCustomerBy() throws Exception {
        String uri = V1 + root  + "/ids-customer";
        Long shopId = 1L;
        String searchKeywords = "123";
        String customerPhone = "456987";
        List<Long> ids = Arrays.asList(1L);
        Pageable pageable = PageRequest.of(0,5);
        List<Long> customers = new ArrayList<>();
        double count = Math.ceil(ids.size()/1000.0) - 1;
        int max = 0;
        for(int i = 0; i <= count; i++) {
            if ((i + 1)*1000 > ids.size()) {
                max = ids.size();
            } else {
                max = (i + 1)*1000;
            }
            List<Long> subIds = ids.subList(i*1000, max);
            Mockito.when(repository.getCustomerIds( searchKeywords, customerPhone, subIds))
                    .thenReturn(customers);
        }

        serviceImpl.getIdCustomerBy(searchKeywords, customerPhone, ids);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void testGetCustomerDefault() throws Exception {
        String uri = V1 + root  + "/feign-default/{id}";
        Long shopId = 1L;
        Pageable pageable = PageRequest.of(0,5);
        List<CustomerDTO> lstCustomerDTO = Arrays.asList(new CustomerDTO());
        Mockito.when(repository.getCustomerDefault(shopId))
                .thenReturn(lstCustomerDTO);
        serviceImpl.getCustomerDefaultByShop(shopId);

        ResultActions resultActions = mockMvc
                .perform(get(uri,shopId).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCustomerInfo() throws Exception {
        String uri = V1 + root  + "/feign-cusinfo";
        Integer status = 1;
        List<Long> customerIds = Arrays.asList(1L);
        List< SortDTO > sorts = new ArrayList<>();
        SortDTO sortDTO = new SortDTO();
        sortDTO.setDirection("DESC");
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(cb);

        CriteriaQuery<Customer> query = Mockito.mock(CriteriaQuery.class);
        Mockito.when(cb.createQuery(Customer.class)).thenReturn(query);

        Root<Customer> customer = Mockito.mock(Root.class);
        Mockito.when(query.from(Customer.class)).thenReturn(customer);

        Path fieldName = Mockito.mock(Path.class);
        Mockito.when(customer.get("id")).thenReturn(fieldName);

        TypedQuery<Customer> typedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(query)).thenReturn(typedQuery);

        serviceImpl.getCustomerInfo(status, customerIds, sorts);

        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(this.mapToJson(sorts))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findCustomerForSaleCase1() throws Exception {
        String uri = V1 + root  + "/autocomplete";
        Long customerId = 1L;
        Long shopId = 1L;
        Pageable pageable = PageRequest.of(0,5);
        CustomerSaleFilter customerFilter = new CustomerSaleFilter();
        customerFilter.setSearchKeywords("123");
        customerFilter.setCustomerOfShop(true);
        customerFilter.setSearchPhoneOnly(true);

        List<CustomerDTO> lstCustomerDTO = new ArrayList<>();
        CustomerDTO customerDTO = new CustomerDTO();
        lstCustomerDTO.add(customerDTO);
        Page<CustomerDTO> page = new PageImpl<>(lstCustomerDTO, pageable, lstCustomerDTO.size());
        Mockito.when(repository.searchForSaleFone(shopId, customerFilter.getSearchKeywords(), pageable)).thenReturn(page);

        serviceImpl.findCustomerForSale(shopId, customerFilter, pageable);

        ResultActions resultActions = mockMvc
                .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findCustomerForSaleCase2() throws Exception {
        String uri = V1 + root  + "/autocomplete";
        Long customerId = 1L;
        Long shopId = 1L;
        Pageable pageable = PageRequest.of(0,5);
        CustomerSaleFilter customerFilter = new CustomerSaleFilter();
        customerFilter.setSearchKeywords("123");
        customerFilter.setCustomerOfShop(false);
        customerFilter.setSearchPhoneOnly(false);

        List<CustomerDTO> lstCustomerDTO = new ArrayList<>();
        CustomerDTO customerDTO = new CustomerDTO();
        lstCustomerDTO.add(customerDTO);
        Page<CustomerDTO> page = new PageImpl<>(lstCustomerDTO, pageable, lstCustomerDTO.size());
        Mockito.when(repository.searchForSale(null, VNCharacterUtils.removeAccent(customerFilter.getSearchKeywords()).toUpperCase(),
                customerFilter.getSearchKeywords(), customerFilter.getSearchKeywords(), pageable)).thenReturn(page);

        serviceImpl.findCustomerForSale(shopId, customerFilter, pageable);

        ResultActions resultActions = mockMvc
                .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getScoreCumulated() throws Exception {
        String uri = V1 + root  + "/scorecumulated/{customerId}";
        Long customerId = 1L;

        MemberCustomer memberCustomer = new MemberCustomer();
        Mockito.when(memBerCustomerRepos.findByCustomerId(customerId)).thenReturn(Optional.of(memberCustomer));

        serviceImpl.getScoreCumulated(customerId);

        ResultActions resultActions = mockMvc
                .perform(get(uri,customerId).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findCustomerForRedInvoice() throws Exception {
        String uri = V1 + root  + "/red-invoice";
        CusRedInvoiceFilter filter = new CusRedInvoiceFilter();
        Long shopId = 1L;
        Pageable pageable = PageRequest.of(0,5);
        List<CustomerDTO> lstCustomerDTO = Arrays.asList(new CustomerDTO());
        Page<CustomerDTO> response = new PageImpl<>(lstCustomerDTO, pageable, lstCustomerDTO.size());
        Mockito.when(repository.searchForRedInvoice(
                filter.getSearchKeywords(), filter.getMobiphone(), filter.getWorkingOffice(), filter.getOfficeAddress(), filter.getTaxCode(), shopId, pageable))
                .thenReturn(response);
        serviceImpl.findCustomerForRedInvoice(filter, shopId, pageable);

        ResultActions resultActions = mockMvc
                .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getCustomerDefault() throws Exception {
        String uri = V1 + root  + "/scorecumulated/{customerId}";
        Long customerId = 1L;

        MemberCustomer memberCustomer = new MemberCustomer();
        Mockito.when(memBerCustomerRepos.findByCustomerId(customerId)).thenReturn(Optional.of(memberCustomer));

        serviceImpl.getScoreCumulated(customerId);

        ResultActions resultActions = mockMvc
                .perform(get(uri,customerId).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findCustomerForSale() throws Exception {
        String uri = V1 + root  + "/autocomplete";
        Long shopId = 1L;
        String searchKeywords = "123";
        Boolean customerOfShop = true;
        Boolean searchPhoneOnly = true;
        Boolean searchAddressOnly = true;
        Pageable pageable = Pageable.unpaged();
        CustomerSaleFilter filter = new CustomerSaleFilter();
        filter.setCustomerOfShop(customerOfShop);
        filter.setSearchPhoneOnly(searchPhoneOnly);
        filter.setSearchKeywords(VNCharacterUtils.removeAccent(searchKeywords.trim()).toUpperCase());

        Long shop = null;
        if(filter.isCustomerOfShop()) shop = shopId;
        CustomerDTO customer = new CustomerDTO();
        List<CustomerDTO> lst = Arrays.asList(customer);
        Page<CustomerDTO> response = new PageImpl<>(lst, pageable, lst.size());
        Mockito.when(repository.searchForSaleFone(shop, filter.getSearchKeywords(), pageable)).thenReturn(response);

        serviceImpl.findCustomerForSale(shopId, filter, pageable);

        ResultActions resultActions = mockMvc
                .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findCustomerForSale1() throws Exception {
        String uri = V1 + root  + "/autocomplete";
        Long shopId = 1L;
        String searchKeywords = "123";
        Boolean customerOfShop = false;
        Boolean searchPhoneOnly = false;
        Boolean searchAddressOnly = false;
        Pageable pageable = Pageable.unpaged();
        CustomerSaleFilter filter = new CustomerSaleFilter();
        filter.setCustomerOfShop(customerOfShop);
        filter.setSearchPhoneOnly(searchPhoneOnly);
        filter.setSearchKeywords(VNCharacterUtils.removeAccent(searchKeywords.trim()).toUpperCase());

        Long shop = null;
        if(filter.isCustomerOfShop()) shop = shopId;
        CustomerDTO customer = new CustomerDTO();
        List<CustomerDTO> lst = Arrays.asList(customer);
        Page<CustomerDTO> response = new PageImpl<>(lst, pageable, lst.size());
        Mockito.when(repository.searchForSale(shop, VNCharacterUtils.removeAccent(filter.getSearchKeywords()).toUpperCase(),
                filter.getSearchKeywords(), filter.getSearchKeywords(), pageable)).thenReturn(response);

        serviceImpl.findCustomerForSale(shopId, filter, pageable);

        ResultActions resultActions = mockMvc
                .perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
