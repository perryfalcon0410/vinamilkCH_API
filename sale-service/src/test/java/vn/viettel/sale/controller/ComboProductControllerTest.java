package vn.viettel.sale.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.repository.ComboProductRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.service.ComboProductService;
import vn.viettel.sale.service.dto.ComboProductDTO;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.impl.ComboProductServiceImpl;
import vn.viettel.sale.specification.ComboProductSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ComboProductControllerTest extends BaseTest {
    private final String root = "/sales/combo-products";
    private final String uri = V1 + root;

    @InjectMocks
    ComboProductServiceImpl serviceImp;

    @Mock
    ComboProductService service;

    @Mock
    ComboProductRepository repository;

    @Mock
    ProductPriceRepository productPriceRepo;

    private List<ComboProduct> lstEntities;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ComboProductController controller = new ComboProductController();
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        lstEntities = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            final ComboProduct entity = new ComboProduct();
            entity.setId((long) i);
            entity.setProductName("keyword" + i);
            entity.setStatus(1);
            entity.setRefProductId(1L);
            lstEntities.add(entity);
        }
    }

    //-------------------------------findComboProducts-------------------------------
    @Test
    public void findComboProductsTest() throws Exception {
//        when(shopClient.getShopByIdV1(id)).thenReturn(new Response<ShopDTO>().withData(shopDTO));
//        List<Price> prices = new ArrayList<Price>();
//        Price price = new Price();
//        price.setProductId(1L);
//        when(productPriceRepo.findProductPriceWithType(Arrays.asList(1L), 1L, null)).thenReturn(prices);
//        doReturn(lstEntities).when(repository).findAll(Specification.where(ComboProductSpecification.hasKeyWord("keyWord")).and(ComboProductSpecification.hasStatus(1)));
        List<ComboProductDTO> dtos = serviceImp.findComboProducts(1L, "keyword", 1);

//        assertEquals(0, dtos.size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getComboProduct-------------------------------
    @Test
    public void getComboProductTest() throws Exception {
        String url = uri + "/{id}";

        ComboProductDTO result =  new ComboProductDTO();

        given(service.getComboProduct(any(), any())).willReturn(result);

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
