package vn.viettel.sale.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.repository.ComboProductDetailRepository;
import vn.viettel.sale.repository.ComboProductRepository;
import vn.viettel.sale.repository.ProductPriceRepository;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.service.ComboProductService;
import vn.viettel.sale.service.dto.ComboProductDTO;
import vn.viettel.sale.service.impl.ComboProductServiceImpl;
import vn.viettel.sale.specification.ComboProductSpecification;

import javax.persistence.criteria.*;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
        this.setupAction(controller);

        lstEntities = new ArrayList<>();
        prices = new ArrayList<>();
        products = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            final ComboProduct entity = new ComboProduct();
            entity.setId((long) i);
            entity.setProductName("KEYWORD" + i);
            entity.setStatus(1);
            entity.setRefProductId((long) i);
            lstEntities.add(entity);

            final Price price = new Price();
            price.setId((long) i);
            price.setProductId((long) i);
            price.setPrice(5.0);
            prices.add(price);

            final Product product = new Product();
            product.setId((long) i);
            products.add(product);
        }

        comboProductDetails = new ArrayList<>();
        final ComboProductDetail comboProductDetail = new ComboProductDetail();
        comboProductDetail.setProductId(1L);
        comboProductDetail.setId(1L);
        comboProductDetails.add(comboProductDetail);
    }

    //-------------------------------findComboProducts-------------------------------
    @Test
    public void findComboProductsTest() throws Exception {
        String keyWord = "KEYWORD";
        Integer status = 1;
        Long customerTypeId = null;

        when(repository.findAll(any(Specification.class))).thenReturn(lstEntities);

        List<Long> lstProductIds = lstEntities.stream().map(item -> item.getRefProductId()).distinct().collect(Collectors.toList());

        LocalDateTime now = DateUtils.convertToDate(LocalDateTime.now());

        given(productPriceRepo.findProductPriceWithType(lstProductIds, customerTypeId,
                now)).willReturn(prices);
        List<ComboProductDTO> dtos = serviceImp.findComboProducts(1L, keyWord, status);

        assertEquals(lstEntities.size(), dtos.size());

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
