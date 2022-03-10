package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.CustomerNotTradeFilter;
import vn.viettel.report.messaging.ShopExportFilter;
import vn.viettel.report.messaging.TotalReport;
import vn.viettel.report.service.ReportExportGoodsService;
import vn.viettel.report.service.dto.*;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.ReportExportGoodsServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportExportGoodsControllerTest extends BaseTest {
    private final String root = "/reports/export-goods";

    @Spy
    @InjectMocks
    ReportExportGoodsServiceImpl reportExportGoodsService;

    @Mock
    ShopClient shopClient;

    @Mock
    ReportExportGoodsService service;

    private List<ShopExportDTO>  lstEntities;

    private ShopExportFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ReportExportGoodsController controller = new ReportExportGoodsController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            ShopExportDTO ex = new ShopExportDTO();
            ex.setId(i);
            ex.setCatId(+ i);
            ex.setOrderId(i);
            ex.setConvfact("HOP");
            ex.setInternalNumber("IN000" + i);
            ex.setPoNumber("PO000" + i);
            ex.setQuantity(100);
            ex.setProductCode("SP000" + i);
            ex.setProductName("Sản phẩm " + i);
            ex.setTypess(0);
            lstEntities.add(ex);
        }
        filter = new ShopExportFilter();
        filter.setShopId(1L);
    }

    @Test
    public void getAllExportGood() throws Exception{
        String uri = V1 + root;

        Pageable pageable = PageRequest.of(0, 10);
        doReturn(lstEntities).when(reportExportGoodsService).callProcedure(filter);

        CoverResponse<Page<ShopExportDTO>, TotalReport> response = reportExportGoodsService.index(filter, pageable);
        Page<ShopExportDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(),datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }


    @Test
    public void getDataToPrint() throws Exception{
        String uri = V1 + root + "/print";

        doReturn(lstEntities).when(reportExportGoodsService).callProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        PrintShopExportDTO response = reportExportGoodsService.getDataToPrint(filter);
        List<OrderExportDTO> orderImports = response.getExpPO().getOrderImports();

        assertEquals(lstEntities.size(),orderImports.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}