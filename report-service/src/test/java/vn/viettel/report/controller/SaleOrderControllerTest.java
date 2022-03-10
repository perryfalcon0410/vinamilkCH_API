package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.CustomerNotTradeFilter;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.SaleOrderAmountService;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.dto.TableDynamicDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.SaleOrderAmountServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockitoSession;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SaleOrderControllerTest extends BaseTest {
    private final String root = "/reports/customers/sales";

    @Spy
    @InjectMocks
    SaleOrderAmountServiceImpl saleOrderAmountService;

    private TableDynamicDTO tableDynamicDTO;

    @Mock
    SaleOrderAmountService service;

    private SaleOrderAmountFilter filter;

    private List<Object[]> rowData;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final SaleOrderController controller = new SaleOrderController();
        controller.setService(service);
        this.setupAction(controller);
        tableDynamicDTO =  new TableDynamicDTO();
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
        tableDynamicDTO.setTotals(rowData.get(rowData.size() - 1));
        tableDynamicDTO.setResponse(rowData);

        filter = new SaleOrderAmountFilter();
        filter.setNameOrCodeCustomer("CODE");
        filter.setShopId(1L);
    }

    @Test
    public void findAmounts() throws Exception{
        String uri = V1 + root + "/amount";
        Pageable page = PageRequest.of(0, 10);
        doReturn(tableDynamicDTO).when(saleOrderAmountService).callProcedure(filter);
        doReturn(true).when(saleOrderAmountService).validMonth(filter);

        TableDynamicDTO  response = saleOrderAmountService.findAmounts(filter, page);
        Page<Object[]> datas = (Page<Object[]>) response.getResponse();

        assertEquals(rowData.size(), datas.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
            .param("fromDate", "01/04/2021")
            .param("toDate", "01/05/2021"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }


}