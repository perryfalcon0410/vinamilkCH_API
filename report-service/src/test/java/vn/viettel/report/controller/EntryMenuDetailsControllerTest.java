package vn.viettel.report.controller;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.EntryMenuDetailsReportService;
import vn.viettel.report.service.dto.EntryMenuDetailsDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EntryMenuDetailsControllerTest extends BaseTest {
    private final String root = "/reports/entryMenuDetails";

    @MockBean
    private EntryMenuDetailsReportService service;

    @Test
    public void getReportReturnGoods() throws Exception{
        String uri = V1 + root;

        int size = 2;
        int page = 5;

        PageRequest pageRequest = PageRequest.of(page,size);
        List<EntryMenuDetailsDTO> dtoList = Arrays.asList(new EntryMenuDetailsDTO(), new EntryMenuDetailsDTO());
        CoverResponse<Page<EntryMenuDetailsDTO> , ReportTotalDTO> dtoPage = new CoverResponse<>();

//        given(service.getEntryMenuDetailsReport(any(), Mockito.any(PageRequest.class))).willReturn(new Response<CoverResponse<Page<EntryMenuDetailsDTO>, ReportTotalDTO>>().withData(dtoPage));
    }

    @Test
    public void exportToExcel() {
    }

    @Test
    public void getDataPrint() {
    }
}