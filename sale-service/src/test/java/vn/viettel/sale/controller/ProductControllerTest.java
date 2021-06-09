package vn.viettel.sale.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.service.ProductService;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.dto.OrderProductsDTO;
import vn.viettel.sale.service.dto.ProductDTO;
import vn.viettel.sale.service.dto.ProductInfoDTO;

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

    @MockBean
    ProductService productService;

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

        given(productService.findAllProductInfo(any(), any(),any())).willReturn(pageDto);

        ResultActions resultActions = mockMvc
                .perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
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
        given(productService.findProducts(any(), Mockito.any(PageRequest.class))).willReturn(data);

        ResultActions resultActions = mockMvc
                .perform(get(uri)
                        .param("customerTypeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
        String responseData = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(responseData, containsString("\"pageNumber\":" + page));
        assertThat(responseData, containsString("\"pageSize\":" + size));
    }

    //-------------------------------getProduct---------------------------------------
    @Test
    public void getProductSuccessV1Test() throws Exception {
        String uri = V1 + root + "/{id}";

        OrderProductDTO dtoObj = new OrderProductDTO();
        dtoObj.setId(1L);
        given( productService.getProduct(any(), any(), any())).willReturn(dtoObj);
        ResultActions resultActions =  mockMvc
                .perform(MockMvcRequestBuilders.get(uri, 1)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .param("customerTypeId", "1"))
                .andDo(MockMvcResultHandlers.print());
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
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

        given(productService.findProductsMonth(any(),any(), any())).willReturn(pageDto);

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .param("customerTypeId", "1"))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
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

        given(productService.findProductsCustomerTopSale(any(),any(), any())).willReturn(pageDto);

        ResultActions resultActions = mockMvc.perform(get(uri,1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("customerTypeId", "1"))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //-------------------------------changeCustomerType-------------------------------
    @Test
    public void changeCustomerTypeSuccessV1Test() throws Exception {
        String uri = V1 + root + "/change/customer-type/{customerTypeId}";

        OrderProductsDTO dtoObj = new OrderProductsDTO();
        given( productService.changeCustomerType(any(), any(), any())).willReturn(dtoObj);
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

        given(productService.findProductsByKeyWord(any())).willReturn(new Response<List<OrderProductDTO>>().withData(lstDto));

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
        given(productService.findProduct(any(),any(),any(),any())).willReturn(new Response<Page<ProductDTO>>().withData(pageDto));

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertThat(mvcResult.getResponse().getContentAsString(), containsString("data\":{"));
    }




}
