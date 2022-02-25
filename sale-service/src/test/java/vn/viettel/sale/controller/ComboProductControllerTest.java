package vn.viettel.sale.controller;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.ComboProduct;
import vn.viettel.sale.entities.ComboProductDetail;
import vn.viettel.sale.entities.Price;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.repository.ComboProductDetailRepository;
import vn.viettel.sale.repository.ComboProductRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.ComboProductService;
import vn.viettel.sale.service.dto.ComboProductDTO;

import java.time.LocalDateTime;
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
import java.util.stream.Collectors;

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

    @Mock
    ComboProductDetailRepository comboProductDetailRepo;

    @Mock
    ProductRepository productRepo;

    private List<ComboProduct> lstEntities;

    private List<Price> prices;

    private List<ComboProductDetail> comboProductDetails;

    private List<Product> products;

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

        prices = new ArrayList<>();
        final Price price = new Price();
        price.setId(1L);
        price.setProductId(1L);
        prices.add(price);

        comboProductDetails = new ArrayList<>();
        final ComboProductDetail comboProductDetail = new ComboProductDetail();
        comboProductDetail.setProductId(1L);
        comboProductDetail.setId(1L);
        comboProductDetails.add(comboProductDetail);

        products = new ArrayList<>();
        final Product product = new Product();
        product.setId(1L);
        products.add(product);

    }

    //-------------------------------findComboProducts-------------------------------
    @Test
    public void findComboProductsTest() throws Exception {
        List<ComboProductDTO> dtos = serviceImp.findComboProducts(1L, "keyword", 1);

        assertEquals(0, dtos.size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getComboProduct-------------------------------
    @Test
    public void getComboProductTest() throws Exception {
        Long id = lstEntities.get(0).getId();
        String url = uri + "/" + id.toString();

        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        Mockito.when(productPriceRepo.findProductPriceWithType(
                Arrays.asList(lstEntities.get(0).getRefProductId()),
                null,
                DateUtils.convertToDate(LocalDateTime.now())
        )).thenReturn(prices);

        Mockito.when(comboProductDetailRepo.findByComboProductIdAndStatus(1L,1))
                .thenReturn(comboProductDetails);

        Mockito.when(productRepo.getProducts(comboProductDetails
                .stream().map(ComboProductDetail::getProductId).collect(Collectors.toList()), 1))
                .thenReturn(products);

        ComboProductDTO comboProductDTO = serviceImp.getComboProduct(1L, id);

        assertEquals(id, comboProductDTO.getId());

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}
