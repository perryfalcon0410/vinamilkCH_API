package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;



public interface InOutAdjustmentService {
    Page<InOutAdjusmentDTO> find (InOutAdjustmentFilter filter, Pageable pageable);
}
