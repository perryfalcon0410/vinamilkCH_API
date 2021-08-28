package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.ReturnGoodsReportsRequest;
import vn.viettel.report.service.dto.ReportPrintIndustryTotalDTO;
import vn.viettel.report.service.dto.ReportTotalDTO;
import vn.viettel.report.service.dto.ReturnGoodsDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;

public interface ReturnGoodsReportService {
    CoverResponse<Page<ReturnGoodsDTO>, ReportTotalDTO> getReturnGoodsReport(ReturnGoodsReportsRequest filter, Pageable pageable);

    ByteArrayInputStream exportExcel(ReturnGoodsReportsRequest filter) throws IOException, ParseException;

    ReportPrintIndustryTotalDTO getDataPrint(ReturnGoodsReportsRequest filter);
}
