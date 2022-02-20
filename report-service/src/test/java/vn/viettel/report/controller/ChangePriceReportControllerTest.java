package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ChangePriceFilter;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePricePrintDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.ChangePriceReportServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePriceReportControllerTest extends BaseTest {
    private final String root = "/reports/changePrices";

    @Spy
    @InjectMocks
    ChangePriceReportServiceImpl changePriceReportService;

    @Mock
    ShopClient shopClient;

    @Mock
    ChangePriceReportService service;

    private List<ChangePriceDTO> lstEntities;

    private ChangePriceFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ChangePriceReportController controller = new ChangePriceReportController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            ChangePriceDTO price = new ChangePriceDTO();
            price.setId(i);
            price.setProductCode("CUS00" + i);
            price.setProductName("Tên sản phẩm " + i);
            price.setInternalNumber("IN00" + i);
            price.setPoNumber("PO00" + i);
            price.setOrderDate(LocalDateTime.now());
            price.setQuantity(123L);
            price.setTransCode("TNS00" + i);
            lstEntities.add(price);
        }
        filter = new ChangePriceFilter();
        filter.setSearchKey("KH");
        filter.setShopId(1L);
    }

    @Test
    public void index() throws Exception {
        String uri = V1 + root;

        Pageable page = PageRequest.of(0, 10);
        Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>> res = new Response<CoverResponse<List<ChangePriceDTO>,
                ChangePriceTotalDTO>>().withData(new CoverResponse<>(lstEntities, new ChangePriceTotalDTO()));

        doReturn(res).when(changePriceReportService).index(filter, page, false);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        ChangePricePrintDTO response =  changePriceReportService.getAll(filter,  page);
        assertEquals(lstEntities.size(),response.getDetails().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromTransDate", "01/04/2021")
                        .param("toTransDate", "01/05/2021")
                        .param("fromOrderDate", "01/04/2021")
                        .param("toOrderDate", "01/05/2021")
                        .param("isPaging", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

   @Test
    public void getAll() throws Exception {
        String uri = V1 + root + "/pdf";
        Pageable page = PageRequest.of(0, 10);
        Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>> res = new Response<CoverResponse<List<ChangePriceDTO>,
            ChangePriceTotalDTO>>().withData(new CoverResponse<>(lstEntities, new ChangePriceTotalDTO()));

        doReturn(res).when(changePriceReportService).index(filter, page, false);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        ChangePricePrintDTO response =  changePriceReportService.getAll(filter,  page);
        assertEquals(lstEntities.size(),response.getDetails().size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
            .param("fromTransDate", "01/05/2021")
            .param("toTransDate", "01/05/2021"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
       assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }


}