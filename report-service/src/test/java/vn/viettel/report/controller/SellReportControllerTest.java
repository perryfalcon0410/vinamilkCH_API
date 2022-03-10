package vn.viettel.report.controller;

import liquibase.pro.packaged.U;
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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.feign.UserClient;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.SellsReportsRequest;
import vn.viettel.report.messaging.UserDataResponse;
import vn.viettel.report.service.SellsReportService;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.dto.SellDTO;
import vn.viettel.report.service.dto.SellTotalDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.SellsReportServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SellReportControllerTest extends BaseTest {
    private final String root = "/reports/sells";

    @Spy
    @InjectMocks
    SellsReportServiceImpl sellsReportService;

    @Mock
    ShopClient shopClient;

    @Mock
    UserClient userClient;

    @Mock
    SellsReportService service;

    private List<SellDTO> lstEntities;

    private  List<UserDTO> dtoList;

    private SellsReportsRequest filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final SellReportController controller = new SellReportController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();

        dtoList = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            SellDTO sell = new SellDTO();
            sell.setId(i);
            sell.setCustomerCode("CUS00" + i);
            sell.setCustomerName("TÊN KH " + i);
            sell.setOrderNumber("OD00" + i);
            sell.setIndustry("ID" + i);
            sell.setPrice(1000D);
            sell.setQuantity(100);
            lstEntities.add(sell);

            UserDTO dto = new UserDTO();
            dto.setId(1L);
            dto.setFullName("Ten la: " + i);
            dtoList.add(dto);
        }
        filter = new SellsReportsRequest();
        filter.setShopId(1L);
    }

    @Test
    public void getReportSells() throws Exception{
        String uri = V1 + root;
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(lstEntities).when(sellsReportService).callStoreProcedure(filter);

        CoverResponse<Page<SellDTO>, SellTotalDTO> response =  sellsReportService.getSellReport(filter, pageable);
        Page<SellDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(), datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
            .param("fromDate","2021/07/01")
            .param("toDate","2021/07/13")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }


    @Test
    public void getDataPrint() throws Exception{
        String uri = V1 + root + "/print";
        doReturn(lstEntities).when(sellsReportService).callStoreProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        CoverResponse<List<SellDTO>, ReportDateDTO> response = sellsReportService.getDataPrint(filter);
        List<SellDTO> datas = response.getResponse();
        assertEquals(lstEntities.size(), datas.size());

        ResultActions resultActions = mockMvc.perform(get(uri)
            .param("fromDate","2021/07/01")
            .param("toDate","2021/07/13")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getDataUser() throws Exception{
        String uri = V1 + root  + "/get-data-user";
        doReturn(dtoList).when(userClient).getUserDataV1(filter.getShopId());

        List<UserDataResponse> response = sellsReportService.getDataUser(1L);

        assertEquals(dtoList.size(), response.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

}