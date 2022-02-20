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
import vn.viettel.report.messaging.ReturnGoodsReportsRequest;
import vn.viettel.report.service.ReturnGoodsReportService;
import vn.viettel.report.service.dto.ReportPrintIndustryTotalDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;
import vn.viettel.report.service.dto.ReturnGoodsDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.ReturnGoodsReportServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReturnGoodsReportControllerTest extends BaseTest {
    private final String root = "/reports/returnGoods";

    @Spy
    @InjectMocks
    ReturnGoodsReportServiceImpl returnGoodsReportService;

    @Mock
    ShopClient shopClient;

    @Mock
    ReturnGoodsReportService service;

    private List<ReturnGoodsDTO> lstEntities;

    private ReturnGoodsReportsRequest filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ReturnGoodsReportController controller = new ReturnGoodsReportController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            ReturnGoodsDTO dto = new ReturnGoodsDTO();
            dto.setId(i);
            dto.setCustomerCode("CUS00" + i);
            dto.setProductCode("SP00 " + i);
            dto.setProductName("Sản phẩm" + i);
            dto.setReasonForPayment("BREAKITEM");
            dto.setAmount(10000D);
            dto.setTotalAmount(10000000D);
            dto.setTotalRefunds(10000000D);
            dto.setIndustryId(i);
            dto.setIndustry("ID" + i);
            dto.setReturnId(i);
            dto.setQuantity(123);
            dto.setRefunds(10000D);

            lstEntities.add(dto);
        }
        filter = new ReturnGoodsReportsRequest();
        filter.setShopId(1L);
    }

    @Test
    public void getReportReturnGoods() throws Exception{
        String uri = V1 + root;
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(lstEntities).when(returnGoodsReportService).callStoreProcedure(filter);

        CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO>
            response = returnGoodsReportService.getReturnGoodsReport(filter, pageable);
        Page<ReturnGoodsDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(),datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate","2021/07/01")
                .param("toDate","2021/07/13")
                .param("reason","BREAKITEM")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }


    @Test
    public void getDataPrint() throws Exception{
        String uri = V1 + root + "/print";

        doReturn(lstEntities).when(returnGoodsReportService).callStoreProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        ReportPrintIndustryTotalDTO response = returnGoodsReportService.getDataPrint(filter);
        assertEquals(lstEntities.size() - 2,response.getData().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate","2021/07/01")
                        .param("toDate","2021/07/13")
                        .param("reason","BREAKITEM")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}