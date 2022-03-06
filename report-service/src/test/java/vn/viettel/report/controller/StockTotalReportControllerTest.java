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
import vn.viettel.report.messaging.StockTotalFilter;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalCatDTO;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;
import vn.viettel.report.service.dto.StockTotalReportPrintDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.StockTotalReportServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StockTotalReportControllerTest extends BaseTest {
    private final String root = "/reports/stock-total";

    @Spy
    @InjectMocks
    StockTotalReportServiceImpl stockTotalReportService;

    @Mock
    ShopClient shopClient;

    @Mock
    StockTotalReportService service;

    private List<StockTotalReportDTO> lstEntities;

    private StockTotalFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final StockTotalReportController controller = new StockTotalReportController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            StockTotalReportDTO stock = new StockTotalReportDTO();
            stock.setId(i);
            stock.setCatId(i);
            stock.setProductCode("SP00" + i);
            stock.setProductName("Tên sản phẩm " + i);
            stock.setPrice(10000D);
            stock.setStockQuantity(10000L);
            stock.setUnit("HOP");
            lstEntities.add(stock);
        }
        filter = new StockTotalFilter();
        filter.setShopId(1L);
    }


    @Test
    public void getStockTotalReport() throws Exception{
        String uri = V1 + root;
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(lstEntities).when(stockTotalReportService).callProcedure(filter);

        CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO>
                response = stockTotalReportService.getStockTotalReport(filter, pageable);
        Page<StockTotalReportDTO> datas = response.getResponse();

        assertEquals(lstEntities.size() -2, datas.getContent().size());
        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("stockDate" , "01/05/2021")
                .param("warehouseTypeId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }


    @Test
    public void print() throws Exception {
        String uri = V1 + root + "/print";
        doReturn(lstEntities).when(stockTotalReportService).callProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        StockTotalReportPrintDTO response = stockTotalReportService.print(filter);

        List<StockTotalCatDTO> dataByCat = response.getDataByCat();

        assertEquals(lstEntities.size() - 2, dataByCat.size());
        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("stockDate", "01/05/2021")
                        .param("warehouseTypeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

}