package vn.viettel.report.service;

import vn.viettel.report.messaging.SaleDeliveryTypeFilter;

import java.io.ByteArrayInputStream;

public interface SaleDeliveryTypeService {
    ByteArrayInputStream exportExcel(SaleDeliveryTypeFilter filter) throws Exception;
}
