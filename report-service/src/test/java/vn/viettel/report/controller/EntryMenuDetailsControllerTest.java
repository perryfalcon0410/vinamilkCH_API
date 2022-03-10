package vn.viettel.report.controller;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.EntryMenuDetailsReportsRequest;
import vn.viettel.report.service.EntryMenuDetailsReportService;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ReportDateDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.EntryMenuDetailsServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EntryMenuDetailsControllerTest extends BaseTest {
    private final String root = "/reports/entryMenuDetails";

    @Spy
    @InjectMocks
    EntryMenuDetailsServiceImpl entryMenuDetailsService;

    @Mock
    ShopClient shopClient;

    @Mock
    private EntryMenuDetailsReportService service;

    private List<EntryMenuDetailsDTO> lstEntities;

    private EntryMenuDetailsReportsRequest filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final EntryMenuDetailsController controller = new EntryMenuDetailsController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            EntryMenuDetailsDTO entry = new EntryMenuDetailsDTO();
            entry.setId(i);
            entry.setAmount(1000F + i);
            entry.setInternalNumber("IN00" + i);
            entry.setPoNumber("PO00" + i);
            entry.setRedInvoiceNo("RED00" + i);
            entry.setBillDate(LocalDateTime.now());
            entry.setDateOfPayment(LocalDateTime.now());
            entry.setTotalAmount(10000F + i);
            lstEntities.add(entry);
        }
        filter = new EntryMenuDetailsReportsRequest();
        filter.setShopId(1L);

    }

    @Test
    public void getReportEntryMenuDetail() throws Exception{
        String uri = V1 + root;

        doReturn(lstEntities).when(entryMenuDetailsService).callStoreProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO> response =  entryMenuDetailsService.getEntryMenuDetails(filter);
        assertEquals(lstEntities.size(),response.getResponse().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .param("fromDate","2021/07/01")
                .param("toDate","2021/07/01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void getDataPrint() throws Exception{
        String uri = V1 + root + "/print";
        doReturn(lstEntities).when(entryMenuDetailsService).callStoreProcedure(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        CoverResponse<List<EntryMenuDetailsDTO>, ReportDateDTO> responsePrint =  entryMenuDetailsService.getEntryMenuDetails(filter);
        assertEquals(lstEntities.size(),responsePrint.getResponse().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                        .param("fromDate","2021/05/01")
                        .param("toDate","2021/05/01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}