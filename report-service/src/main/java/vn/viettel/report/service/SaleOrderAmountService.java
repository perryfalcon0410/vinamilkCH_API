package vn.viettel.report.service;

import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;

public interface SaleOrderAmountService {
    TableDynamicDTO findAmounts(SaleOrderAmountFilter filter);
}
