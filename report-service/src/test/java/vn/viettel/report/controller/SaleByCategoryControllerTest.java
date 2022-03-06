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
import vn.viettel.core.dto.UserDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.feign.UserClient;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.SaleCategoryFilter;
import vn.viettel.report.messaging.SellsReportsRequest;
import vn.viettel.report.service.SaleByCategoryReportService;
import vn.viettel.report.service.SellsReportService;
import vn.viettel.report.service.dto.SaleByCategoryPrintDTO;
import vn.viettel.report.service.dto.SalesByCategoryReportDTO;
import vn.viettel.report.service.dto.SellDTO;
import vn.viettel.report.service.dto.SellTotalDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.SaleByCategoryImpl;
import vn.viettel.report.service.impl.SellsReportServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SaleByCategoryControllerTest extends BaseTest {
    private final String root = "/reports/sale-category";

    @Spy
    @InjectMocks
    SaleByCategoryImpl saleByCategoryReportService;

    @Mock
    ShopClient shopClient;

    @Mock
    SaleByCategoryReportService service;

    private SalesByCategoryReportDTO salesByCategoryReportDTO;

    private SaleCategoryFilter filter;

    private List<Object[]> rowData;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final SaleByCategoryController controller = new SaleByCategoryController();
        controller.setService(service);
        this.setupAction(controller);
        salesByCategoryReportDTO = new SalesByCategoryReportDTO();
        rowData = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            Object[] rowDatas = new Object[5];
            rowDatas[0] = "2021-05-01 22:20:20";
            rowDatas[1] = "PO00" + i;
            rowDatas[2] = "RED00"  + i;
            rowDatas[3] = LocalDateTime.now();
            rowDatas[4] = LocalDateTime.now();
            rowData.add(rowDatas);
        }
        salesByCategoryReportDTO.setResponse(rowData);
        filter = new SaleCategoryFilter();
        filter.setShopId(1L);
        filter.setFromDate(LocalDateTime.now());
        filter.setToDate(LocalDateTime.now());
    }

    @Test
    public void getReportSaleByCategory() throws Exception{
        String uri = V1 + root;
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(salesByCategoryReportDTO).when(saleByCategoryReportService).callProcedure(filter);

        SalesByCategoryReportDTO response =  saleByCategoryReportService.getSaleByCategoryReport(filter, pageable);
        Page<Object[]>  datas = (Page<Object[]>) response.getResponse();

        assertEquals(rowData.size(), datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate","2021/07/01")
                        .param("toDate","2021/07/13")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void printReportSaleByCategory() throws Exception{
        String uri = V1 + root + "/print";
        doReturn(salesByCategoryReportDTO).when(saleByCategoryReportService).callProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        SaleByCategoryPrintDTO response =  saleByCategoryReportService.print(filter);
        assertEquals(rowData.size(), response.getReportData().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate","2021/07/01")
                        .param("toDate","2021/07/13")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }




}
