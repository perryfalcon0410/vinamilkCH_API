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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.ReportProductTransService;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.dto.OrderProductsDTO;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.service.dto.ProductInfoDTO;
import vn.viettel.sale.service.impl.ProductServiceImpl;
import vn.viettel.sale.service.impl.ReportProductTransServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ProductControllerTest extends BaseTest {
    private final String root = "/sales/products";

    @InjectMocks
    ProductServiceImpl serviceImp;

    @Mock
    ProductService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ProductController controller = new ProductController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    //-------------------------------findALlProductInfo-------------------------------
    @Test
    public void findComboProductsSuccessTest() throws Exception {
        String uri = V1 + root + "/product-infos";

        List<ProductInfoDTO> lstDto = new ArrayList<>();
        lstDto.add(new ProductInfoDTO());
        lstDto.add(new ProductInfoDTO());

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        Page<ProductInfoDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());

//        given(service.findAllProductInfo(any(), any(),any())).willReturn(pageDto);
        service.findAllProductInfo(any(), any(),any());

        ResultActions resultActions = mockMvc
                .perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------findProducts-------------------------------------
    @Test
    public void findProductsSuccessTest() throws Exception {
        String uri = V1 + root ;
        int size = 2;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<OrderProductDTO> list = Arrays.asList(new OrderProductDTO(), new OrderProductDTO());
        Page<OrderProductDTO> data = new PageImpl<>(list, pageRequest , list.size());
//        given(service.findProducts(any(), Mockito.any(PageRequest.class))).willReturn(data);
        service.findProducts(any(), Mockito.any(PageRequest.class));
        ResultActions resultActions = mockMvc
                .perform(get(uri)
                        .param("customerTypeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------findProductsTopSale------------------------------
    @Test
    public void findProductsTopSaleSuccessTest() throws Exception {
        String uri = V1 + root + "/top-sale/month";
        List<OrderProductDTO> lstDto = new ArrayList<>();
        lstDto.add(new OrderProductDTO());
        lstDto.add(new OrderProductDTO());

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        Page<OrderProductDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());

//        given(service.findProductsMonth(any(),any(), any())).willReturn(pageDto);

        service.findProductsMonth(any(),any(), any());
        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .param("customerTypeId", "1"))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------findProductsCustomerTopSale----------------------
    @Test
    public void findProductsCustomerTopSaleSuccessTest() throws Exception {
        String uri = V1 + root + "/top-sale/customer/{customerId}";
        List<OrderProductDTO> lstDto = new ArrayList<>();
        lstDto.add(new OrderProductDTO());
        lstDto.add(new OrderProductDTO());

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        Page<OrderProductDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());

//        given(service.findProductsCustomerTopSale(any(),any(), any())).willReturn(pageDto);
        service.findProductsCustomerTopSale(any(),any(), any());

        ResultActions resultActions = mockMvc.perform(get(uri,1)
                        .param("page", "5")
                        .param("size", "10")
                        .param("customerTypeId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print());
//        ArgumentCaptor<Pageable> pageableCaptor =
//                ArgumentCaptor.forClass(Pageable.class);
//        verify(characterRepository).findAllPage(pageableCaptor.capture());
//        PageRequest pageable = (PageRequest) pageableCaptor.getValue();
//
//        assertThat(pageable).hasPageNumber(5);
//        assertThat(pageable).hasPageSize(10);
//        assertThat(pageable).hasSort("name", Sort.Direction.ASC);
//        assertThat(pageable).hasSort("id", Sort.Direction.DESC);

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------changeCustomerType-------------------------------
    @Test
    public void changeCustomerTypeSuccessV1Test() throws Exception {
        String uri = V1 + root + "/change/customer-type/{customerTypeId}";

        OrderProductsDTO dtoObj = new OrderProductsDTO();
//        given( service.changeCustomerType(any(), any(), any())).willReturn(dtoObj);
        service.changeCustomerType(any(), any(), any());
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.post(uri, 1)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .param("customerTypeId", "1"))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------findProductsByKeyWord-----------------------------
    @Test
    public void findProductsByKeyWordSuccessTest() throws Exception {
        String uri = V1 + root + "/find";
        List<OrderProductDTO> lstDto = new ArrayList<>();
        lstDto.add(new OrderProductDTO());
        lstDto.add(new OrderProductDTO());

//        given(service.findProductsByKeyWord(any(),any(), any())).willReturn(lstDto);
        service.findProductsByKeyWord(any(),any(), any());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------Choose-product------------------------------------
    @Test
    public void findSuccessTest() throws Exception {
        String uri = V1 + root+ "/choose-product";
        List<ProductDTO> lstDto = new ArrayList<>();
        lstDto.add(new ProductDTO());
        lstDto.add(new ProductDTO());

        int size = 2;
        int page = 5;
        PageRequest pageReq = PageRequest.of(page, size);
        Page<ProductDTO> pageDto = new PageImpl<>(lstDto, pageReq, lstDto.size());
//        given(service.findProduct(any(),any(),any(),any())).willReturn(new Response<Page<ProductDTO>>().withData(pageDto));
        service.findProduct(any(),any(),any(),any(), Mockito.any(Pageable.class));

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
