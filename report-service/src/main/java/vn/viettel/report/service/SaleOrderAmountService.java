package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface SaleOrderAmountService {
    ByteArrayInputStream exportExcel(SaleOrderAmountFilter filter) throws IOException;
    TableDynamicDTO findAmounts(SaleOrderAmountFilter filter, Pageable pageable) ;
    TableDynamicDTO callProcedure(SaleOrderAmountFilter filter) ;

}
