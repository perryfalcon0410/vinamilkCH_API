package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.QuantitySalesReceiptFilter;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface QuantitySalesReceiptService {
    ByteArrayInputStream exportExcel(QuantitySalesReceiptFilter filter) throws IOException;
    TableDynamicDTO findQuantity(QuantitySalesReceiptFilter filter, Pageable pageable) ;
    TableDynamicDTO callProcedure(QuantitySalesReceiptFilter filter) ;
}
