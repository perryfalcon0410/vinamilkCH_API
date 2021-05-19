package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ReportVoucherFilter;
import vn.viettel.report.service.ReportVoucherService;
import vn.viettel.report.service.dto.ReportVoucherDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class ReportVoucherServiceImpl implements ReportVoucherService {

    @Override
    public Response<Page<ReportVoucherDTO>> index(ReportVoucherFilter filter, Pageable pageable) {
        return null;
    }

    @Override
    public ByteArrayInputStream exportExcel(ReportVoucherFilter filter) throws IOException {
        return null;
    }
}
