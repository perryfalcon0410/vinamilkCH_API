package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.service.dto.ReportVoucherDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ReportVoucherService {

    Page<ReportVoucherDTO> index(ReportVoucherFilter filter, Pageable pageable);

    ByteArrayInputStream exportExcel(ReportVoucherFilter filter) throws IOException;
}
