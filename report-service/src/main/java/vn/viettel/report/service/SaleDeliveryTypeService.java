package vn.viettel.report.service;

import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface SaleDeliveryTypeService {
    ByteArrayInputStream exportExcel(SaleDeliveryTypeFilter filter) throws Exception;
    List<ApParamDTO> deliveryType();
}
