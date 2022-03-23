package vn.viettel.sale.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.*;
import vn.viettel.sale.messaging.ComboProductTranDetailRequest;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.repository.*;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.StockTotalService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.TotalDTO;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.impl.ComboProductTransServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ComboProductTransControllerTest extends BaseTest {
    private final String root = "/sales/combo-product-trans";
    private final String uri = V1 + root;

    @InjectMocks
    ComboProductTransServiceImpl serviceImp;

    @Mock
    ComboProductTransService service;

    @Mock
    ComboProductTransRepository repository;

    @Mock
    ShopClient shopClient;

    @Mock
    ComboProductRepository comboProductRepository;

    @Mock
    ProductPriceRepository productPriceRepository;

    @Mock
    ComboProductDetailRepository comboProductDetailRepo;

    @Mock
    StockTotalRepository stockTotalRepo;

    @Mock
    StockTotalService stockTotalService;

    @Mock
    ProductPriceRepository productPriceRepo;

    @Mock
    ProductRepository productRepo;

    @Mock
    ComboProductTransDetailRepository comboProductTransDetailRepo;

    private List<ComboProductTrans> lstEntities;

    private List<ShopDTO> shops;

    private List<ComboProduct> comboProducts;

    private List<Price> prices;

    private List<ComboProductDetail> comboProductDetails;

    private List<Product> products;

    private List<StockTotal> stockTotals;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        serviceImp.setModelMapper(this.modelMapper);
        final ComboProductTransController controller = new ComboProductTransController();
        controller.setService(service);
        this.setupAction(controller);

        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            ComboProductTrans productTrans = new ComboProductTrans();
            productTrans.setId(i);
            productTrans.setTransCode(i.toString());
            lstEntities.add(productTrans);
        }

        shops = new ArrayList<>();
        final ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(1L);
        shopDTO.setShopCode("SHOP1");
        shops.add(shopDTO);

        comboProducts = new ArrayList<>();
        final ComboProduct comboProduct = new ComboProduct();
        comboProduct.setId(1L);
        comboProduct.setRefProductId(1L);
        comboProduct.setStatus(1);
        comboProducts.add(comboProduct);

        prices = new ArrayList<>();
        final Price price = new Price();
        price.setId(1L);
        price.setProductId(1L);
        price.setPriceNotVat(1D);
        prices.add(price);

        comboProductDetails = new ArrayList<>();
        final ComboProductDetail comboProductDetail = new ComboProductDetail();
        comboProductDetail.setProductId(1L);
        comboProductDetail.setId(1L);
        comboProductDetail.setComboProductId(1L);
        comboProductDetail.setFactor(1);
        comboProductDetails.add(comboProductDetail);

        stockTotals = new ArrayList<>();
        final StockTotal stockTotal = new StockTotal();
        stockTotal.setId(1L);
        stockTotal.setProductId(1L);
        stockTotal.setQuantity(10);
        stockTotal.setStatus(1);
        stockTotal.setShopId(1L);
        stockTotals.add(stockTotal);

        products = new ArrayList<>();
        final Product product = new Product();
        product.setId(1L);
        product.setIsCombo(true);
        product.setStatus(1);
        product.setComboProductId(1L);
        product.setProductCode("1");
        product.setProductName("A");
        products.add(product);
    }

    //-------------------------------findComboProductTrans-------------------------------
    @Test
    public void findComboProductTransTest() throws Exception {
        Long shopId = 1L;
        String transCode = "TRANSCODE";
        LocalDateTime fromDate = LocalDateTime.of(2022,2,22,0,0);
        ComboProductTranFilter filter = new ComboProductTranFilter();
        filter.setShopId(shopId);
        filter.setTransCode(transCode);
        filter.setTransType(1);
        filter.setFromDate(DateUtils.convertFromDate(fromDate));
        filter.setToDate(DateUtils.convertToDate(LocalDateTime.now()));

        final Page<ComboProductTrans> pageData = new PageImpl<>(lstEntities);
        Pageable page = PageRequest.of(0, 5);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageData);

        given(repository.getExchangeTotal(shopId, transCode, filter.getTransType(),
                fromDate, filter.getToDate())).willReturn(new TotalDTO());

        CoverResponse<Page<ComboProductTranDTO>, TotalDTO>  result = serviceImp.getAll(filter, page);

        assertNotNull(result);

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate", "2022/02/22")
                        .param("toDate", "2022/02/22")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------create-------------------------------
    @Test
    public void createTest() throws Exception {
        ComboProductTranRequest data = new ComboProductTranRequest();
        data.setTransDate(LocalDateTime.now());
        data.setTransType(1);
        data.setWarehouseTypeId(1L);

        ComboProductTranDetailRequest combo = new ComboProductTranDetailRequest();
        combo.setComboProductId(1L);
        combo.setRefProductId(1L);
        combo.setPrice(10000.0);
        combo.setQuantity(10);
        data.setDetails(Arrays.asList(combo));

        Mockito.when(shopClient.getByIdV1(1L)).thenReturn(new Response<ShopDTO>().withData(shops.get(0)));

        LocalDate currentDate = LocalDate.now();
        Integer yy = currentDate.getYear();
        Integer mm = currentDate.getMonthValue();
        Integer dd = currentDate.getDayOfMonth();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(yy);
        stringBuilder.append(Integer.toString(mm + 100).substring(1));
        stringBuilder.append(Integer.toString(dd + 100).substring(1) + ".");

        Mockito.when(repository.getLastOrderNumber(1L,"ICB.SHOP1." + stringBuilder, PageRequest.of(0,1)))
                .thenReturn((new PageImpl<>(lstEntities)));

        List<Long> ids = Arrays.asList(combo).
                stream().map(item -> item.getComboProductId()).collect(Collectors.toList());

        Mockito.when(comboProductRepository.findAllById(ids))
                .thenReturn(comboProducts);

        Mockito.when(productPriceRepo.findProductPriceWithType(
                Arrays.asList(combo.getRefProductId()),
                null,
                DateUtils.convertToDate(LocalDateTime.now())
        )).thenReturn(prices);

        ComboProductTranDTO comboProductTranDTO = serviceImp.create(data, 1L, "admin1");

        assertEquals(data.getTransType(), comboProductTranDTO.getTransType());

        String inputJson = super.mapToJson(data);
        ResultActions resultActions = mockMvc.perform(post(uri)
                .content(inputJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    //-------------------------------getComboProductTran-------------------------------
    @Test
    public void getComboProductTranTest() throws Exception {
        Long id = lstEntities.get(0).getId();
        String url = uri + "/" + id.toString();

        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(lstEntities.get(0)));

        ComboProductTranDTO result = serviceImp.getComboProductTrans(id);

        assertEquals(id, result.getId());

        ResultActions resultActions = mockMvc.perform(get(url, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
