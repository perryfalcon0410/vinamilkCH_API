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
import vn.viettel.core.exception.ValidateException;
import vn.viettel.report.BaseTest;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.service.ReportVoucherService;
import vn.viettel.report.service.dto.ReportVoucherDTO;
import vn.viettel.report.service.feign.ShopClient;
import vn.viettel.report.service.impl.ReportVoucherServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReportVoucherControllerTest extends BaseTest {
    private final String root = "/reports/vouchers";

    @Spy
    @InjectMocks
    ReportVoucherServiceImpl reportVoucherService;

    @Mock
    ShopClient shopClient;

    @Mock
    ReportVoucherService service;

    private List<ReportVoucherDTO> lstEntities;

    private ReportVoucherFilter filter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final ReportVoucherController controller = new ReportVoucherController();
        controller.setService(service);
        this.setupAction(controller);
        lstEntities = new ArrayList<>();
        for (Long i = 1L; i < 6L; i++) {
            ReportVoucherDTO rp = new ReportVoucherDTO();
            rp.setId(i);
            rp.setCustomerCode("CUS00" + i);
            rp.setVoucherCode("VC00 " + i);
            rp.setVoucherName("Tên voucher" + i);
            rp.setVoucherProgramCode("VPC00" + i);
            rp.setVoucherProgramName("Tên chương trình" + i);
            rp.setShopCode("SHOP101");
            rp.setShopName("Cửa hàng số 1");
            lstEntities.add(rp);
        }
        filter = new ReportVoucherFilter();
        filter.setShopId(1L);
    }

    @Test
    public void getAllExportGood() throws Exception{
        String uri = V1 + root;
        Pageable pageable = PageRequest.of(0, 10);

        doReturn(lstEntities).when(reportVoucherService).callReportVoucherDTO(filter);
        Page<ReportVoucherDTO> response = reportVoucherService.index(filter, pageable);

        assertEquals(lstEntities.size(),response.getContent().size());

        ResultActions resultActions = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        assertEquals(200, resultActions.andReturn().getResponse().getStatus());

    }
}