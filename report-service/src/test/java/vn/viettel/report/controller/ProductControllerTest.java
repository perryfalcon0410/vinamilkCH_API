package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.PromotionProductDTO;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.PromotionProductServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerTest extends BaseTest {
    private final String root = "/reports/products";

    @Spy
    @InjectMocks
    PromotionProductServiceImpl promotionProductService;

    @Mock
    ShopClient shopClient;

    @Mock
    PromotionProductService service;


    private List<PromotionProductDTO> lstEntities;

    private PromotionProductFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ProductController controller = new ProductController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            PromotionProductDTO promo = new PromotionProductDTO();
            promo.setId(i);
            promo.setProductCode("CUS00" + i);
            promo.setProductName("Tên sản phẩm " + i);
            promo.setProductCatName("CAT00" + i);
            promo.setBarCode("BAR00" + i);
            promo.setOnlineNumber("ONL00" + i);
            promo.setOrderDate(LocalDateTime.now());
            promo.setQuantity(123);
            promo.setPrice(10000D);
            lstEntities.add(promo);
        }
        filter = new PromotionProductFilter();
        filter.setShopId(1L);
    }


    @Test
    public void findReportPromotionProducts() throws Exception{
        String uri = V1 + root + "/promotions";
        Pageable pageable = PageRequest.of(0, 10);

        doReturn(lstEntities).when(promotionProductService).callStoreProcedure(filter);
        CoverResponse<Page<PromotionProductDTO>, PromotionProductTotalDTO>
            response = promotionProductService.getReportPromotionProducts(filter, pageable);

        Page<PromotionProductDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(),datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate","2021/07/01")
                .param("toDate","2021/07/13")
                .param("productCodes","SP0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void getDataPrint() throws Exception{
        String uri = V1 + root + "/promotions/print";

        doReturn(lstEntities).when(promotionProductService).callStoreProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        PromotionProductReportDTO response = promotionProductService.getDataPrint(filter);
        assertEquals(lstEntities.size(),response.getProductCats().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate","2021/07/01")
                        .param("toDate","2021/07/13")
                        .param("productCodes","SP0001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}