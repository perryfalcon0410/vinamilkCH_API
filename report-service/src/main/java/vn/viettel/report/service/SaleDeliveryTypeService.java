package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.service.dto.SaleDeliTypeTotalDTO;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface SaleDeliveryTypeService {
    ByteArrayInputStream exportExcel(SaleDeliveryTypeFilter filter) throws Exception;
    List<ApParamDTO> deliveryType();
    CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO> getSaleDeliType(SaleDeliveryTypeFilter filter, Pageable pageable);
}
