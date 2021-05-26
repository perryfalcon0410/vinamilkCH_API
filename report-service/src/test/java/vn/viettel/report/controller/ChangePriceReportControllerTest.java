package vn.viettel.report.controller;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import vn.viettel.report.BaseTest;
import vn.viettel.report.service.ChangePriceReportService;

import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

public class ChangePriceReportControllerTest extends BaseTest {
    private final String root = "/reports/changePrices";

    @MockBean
    ChangePriceReportService changePriceReportService;

    @Test
    public void index() {
        String uri = V1 + root;
//        int
//
//        given(changePriceReportService.index(any())).willReturn(response);
    }

    @Test
    public void getAll() {
    }

    @Test
    public void exportToExcel() {
    }
}