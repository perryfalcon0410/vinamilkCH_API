package vn.viettel.sale.service;

import net.sf.jasperreports.engine.JRException;
import vn.viettel.core.service.BaseService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface InvoiceReportService extends BaseService {
     ByteArrayInputStream invoiceReport(Long shopId, String transCode) throws JRException, IOException;
}
