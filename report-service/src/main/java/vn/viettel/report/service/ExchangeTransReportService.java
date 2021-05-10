package vn.viettel.report.service;

import vn.viettel.report.messaging.ExchangeTransFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ExchangeTransReportService {
    ByteArrayInputStream exportExcel(ExchangeTransFilter filter) throws IOException;
}
