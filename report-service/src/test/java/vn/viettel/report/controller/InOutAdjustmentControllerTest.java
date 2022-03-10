package vn.viettel.report.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ExchangeTransFilter;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.service.InOutAdjustmentService;
import vn.viettel.report.service.dto.ExchangeTransReportDTO;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;
import vn.viettel.report.service.dto.InOutAdjustmentTotalDTO;
import vn.viettel.report.service.impl.InOutAdjustmentServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InOutAdjustmentControllerTest extends BaseTest {
    private final String root = "/reports/in-out-adjustment";

    @Spy
    @InjectMocks
    InOutAdjustmentServiceImpl inOutAdjustmentService;

    @Mock
    InOutAdjustmentService service;

    private InOutAdjustmentFilter filter;

    private List<InOutAdjusmentDTO> lstEntities;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final InOutAdjustmentController controller = new InOutAdjustmentController();
        controller.setService(service);
        this.setupAction(controller);

        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            InOutAdjusmentDTO data = new InOutAdjusmentDTO();
            data.setId(i);
            data.setProductCode("SP00" + i);
            data.setProductName("Sản phẩm " + i);
            data.setProductInfoName("Nghành " + i);
            data.setPrice(10000F);
            data.setQuantity(100);
            data.setRedInvoiceNo("RED00" + i);
            lstEntities.add(data);
        }

        filter = new InOutAdjustmentFilter();
        filter.setShopId(1L);
    }

   @Test
    public void find() throws Exception{
        String uri = V1 + root;
        Pageable page = PageRequest.of(0, 10);
        doReturn(new Response<List<InOutAdjusmentDTO>>().withData(lstEntities)).when(inOutAdjustmentService).callProcedure(filter);

        CoverResponse<Page<InOutAdjusmentDTO>, InOutAdjustmentTotalDTO> response = inOutAdjustmentService.find(filter, page);
        Page<InOutAdjusmentDTO> datas = response.getResponse();

        assertEquals(lstEntities.size(),datas.getContent().size());
        ResultActions resultActions = mockMvc.perform(get(uri).contentType(MediaType.APPLICATION_JSON)
            .param("fromDate", "01/05/2021")
            .param("toDate", "01/04/2021"))
           .andExpect(status().isOk())
           .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }


}