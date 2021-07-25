package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;
import vn.viettel.report.service.dto.InOutAdjustmentTotalDTO;

import java.util.List;


public interface InOutAdjustmentService {
    CoverResponse<Page<InOutAdjusmentDTO>, InOutAdjustmentTotalDTO> find (InOutAdjustmentFilter filter, Pageable pageable);
    Response<List<InOutAdjusmentDTO>> callProcedure(InOutAdjustmentFilter filter);
    List<InOutAdjusmentDTO> dataExcel (InOutAdjustmentFilter filter);
}
