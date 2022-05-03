package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
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

    @Mock
    public EntityManager entityManager;

    @Mock
    StoredProcedureQuery storedProcedure;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ChangePriceReportController controller = new ChangePriceReportController();
        controller.setService(service);
        controller.setShopClient(new ShopClient() {
            @Override
            public Response<ShopDTO> getShopByIdV1(Long id) {
                return shop;
            }

            @Override
            public Response<ShopDTO> getByNameV1(String name) {
                return shop;
            }
        });
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
        filter.setFromTransDate(LocalDateTime.now());
        filter.setToTransDate(LocalDateTime.now());
    }

    @Test
    public void index() throws Exception {
        String uri = V1 + root;

        Pageable page = PageRequest.of(0, 10);
        Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>> res = new Response<CoverResponse<List<ChangePriceDTO>,
                ChangePriceTotalDTO>>().withData(new CoverResponse<>(lstEntities, new ChangePriceTotalDTO()));

        when(entityManager.createStoredProcedureQuery("P_CHANGE_PRICE", ChangePriceDTO.class)).thenReturn(storedProcedure);

        Object response =  changePriceReportService.index(filter, page, false);
        assertNotNull(response);

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromTransDate", "01/04/2021")
                        .param("toTransDate", "01/05/2021")
                        .param("fromOrderDate", "01/04/2021")
                        .param("toOrderDate", "01/05/2021")
                        .param("isPaging", "true")
                        .param("licenseNumber", "NUM00")
                        .contentType(MediaType.APPLICATION_JSON))
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
                .param("fromTransDate", "01/04/2021")
                .param("toTransDate", "01/05/2021")
                .param("fromOrderDate", "01/04/2021")
                .param("toOrderDate", "01/05/2021")
                .param("ids", "1,2,3")
                .param("licenseNumber", "NUM00")
                )
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
       assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

}