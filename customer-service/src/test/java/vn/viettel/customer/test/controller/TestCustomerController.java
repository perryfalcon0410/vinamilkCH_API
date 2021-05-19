//package vn.viettel.customer.test.controller;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import vn.viettel.core.dto.customer.CustomerDTO;
//import vn.viettel.core.messaging.Response;
//import vn.viettel.customer.messaging.CustomerFilter;
//import vn.viettel.customer.service.CustomerService;
//import vn.viettel.customer.test.config.CustomerApplication;
//
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//public class TestCustomerController extends CustomerApplication {
//    private final String root = "/customers";
//
//    @MockBean
//    CustomerService customerService;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private MockMvc mockMvc;
//
//    @Before
//    public void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//    }
//
//    @Test
//    public void testGetCustomerById() throws Exception {
//        CustomerDTO customerDTO = new CustomerDTO();
//        customerDTO.setId(1L);
//        customerDTO.setFirstName("Linh");
//        customerDTO.setLastName("Nguyễn Thị Thùy");
//        customerDTO.setShopId(1L);
//        customerDTO.setCustomerCode("CUS.SHOP1.00001");
//        customerDTO.setDob(new Date(1998,06,28));
//        customerDTO.setNameText("NGUYEN THI THUY");
//        customerDTO.setMobiPhone("0941667427");
//        customerDTO.setCustomerTypeId(1L);
//        customerDTO.setStatus(1L);
//
//        Response<CustomerDTO> response = new Response<CustomerDTO>().withData(customerDTO);
//        when(customerService.getCustomerById(1L)).thenReturn(response);
//
//        this.mockMvc.perform(get(V1 + root +"/1"));
//    }
//
//    @Test
//    public void testSearchCustomer() throws Exception {
//        CustomerDTO customerDTO = new CustomerDTO();
//        customerDTO.setId(1L);
//        customerDTO.setFirstName("Linh");
//        customerDTO.setLastName("Nguyễn Thị Thùy");
//        customerDTO.setShopId(1L);
//        customerDTO.setCustomerCode("CUS.SHOP1.00001");
//        customerDTO.setDob(new Date(1998,06,28));
//        customerDTO.setMobiPhone("0941667427");
//        customerDTO.setCustomerTypeId(1L);
//        customerDTO.setStatus(1L);
//
//        CustomerDTO customerDTO1 = new CustomerDTO();
//        customerDTO1.setId(2L);
//        customerDTO1.setFirstName("Trang");
//        customerDTO1.setLastName("Nguyễn Thị Thùy");
//        customerDTO1.setShopId(2L);
//        customerDTO1.setCustomerCode("CUS.SHOP2.00001");
//        customerDTO1.setDob(new Date(1998,06,28));
//        customerDTO1.setMobiPhone("0941667428");
//        customerDTO1.setCustomerTypeId(1L);
//        customerDTO1.setStatus(1L);
//
//
//        CustomerDTO customerDTO2 = new CustomerDTO();
//        customerDTO2.setId(3L);
//        customerDTO2.setFirstName("Minh");
//        customerDTO2.setLastName("Lê Duy");
//        customerDTO2.setShopId(1L);
//        customerDTO2.setCustomerCode("CUS.SHOP1.00002");
//        customerDTO2.setDob(new Date(1998,8,28));
//        customerDTO2.setMobiPhone("0941667426");
//        customerDTO2.setCustomerTypeId(1L);
//        customerDTO2.setStatus(1L);
//
//        List<CustomerDTO> lstCustomer = new ArrayList<>();
//        lstCustomer.add(customerDTO);
//        lstCustomer.add(customerDTO1);
//        lstCustomer.add(customerDTO2);
//
//        CustomerFilter filter = new CustomerFilter();
//        Pageable pageable = null;
//
//        Response<Page<CustomerDTO>> responses = customerService.index(filter, pageable);
//        when(customerService.index(filter, pageable)).thenReturn(responses);
//
//        this.mockMvc.perform(get(V1 + root +"/1"));
//    }
//}
