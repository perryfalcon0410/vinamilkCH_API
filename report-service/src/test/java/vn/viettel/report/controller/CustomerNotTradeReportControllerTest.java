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
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.CustomerNotTradeFilter;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.CustomerNotTradePrintDTO;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.CustomerNotTradeServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerNotTradeReportControllerTest extends BaseTest {
    private final String root = "/reports/customers";

    @Spy
    @InjectMocks
    CustomerNotTradeServiceImpl customerNotTradeService;

    @Mock
    ShopClient shopClient;

    @Mock
    CustomerNotTradeService service;

    private List<CustomerReportDTO> lstEntities;

    private CustomerNotTradeFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final CustomerNotTradeReportController controller = new CustomerNotTradeReportController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            CustomerReportDTO cus = new CustomerReportDTO();
            cus.setId(i);
            cus.setCustomerCode("CUS00" + i);
            cus.setCustomerName("TÊN KH " + i);
            cus.setAddress("Đia chỉ" + i);
            cus.setPhone("02343456" + i);
            cus.setBirthDay(LocalDateTime.now());
            lstEntities.add(cus);
        }
        filter = new CustomerNotTradeFilter();
        filter.setFromDate(new Date());
        filter.setShopId(1L);
    }

    @Test
    public void getCustomerNotTrade() throws Exception{
        String uri = V1 + root + "/not-trade";
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(lstEntities).when(customerNotTradeService).customerNotTradeProcedures(filter);

        Response response1 = ( Response) customerNotTradeService.index(filter, false, pageable);
        List<CustomerReportDTO> listResponse = (List<CustomerReportDTO>) response1.getData();
        assertEquals(lstEntities.size(),listResponse.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
            .param("fromDate", "01/04/2021")
            .param("toDate", "01/05/2021")
            .param("isPaging", "true"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }

    @Test
    public void print() throws Exception{
        String uri = V1 + root + "/print";

        doReturn(lstEntities).when(customerNotTradeService).customerNotTradeProcedures(filter);
        doReturn(shop).when(shopClient).getShopByIdV1(filter.getShopId());

        CustomerNotTradePrintDTO response =  customerNotTradeService.printCustomerNotTrade(filter);
        List<CustomerReportDTO> listResponse = response.getData();
        assertEquals(lstEntities.size(),listResponse.size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
            .param("fromDate", "01/04/2021")
            .param("toDate", "01/05/2021")
            .param("isPaging", "false"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }


}