package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.messaging.RedInvoicePrint;
import vn.viettel.sale.messaging.TotalRedInvoice;
import vn.viettel.sale.messaging.TotalRedInvoiceResponse;
import vn.viettel.sale.service.dto.ProductDetailDTO;
import vn.viettel.sale.service.dto.RedInvoiceDTO;
import vn.viettel.sale.service.dto.RedInvoiceDataDTO;
import vn.viettel.sale.service.dto.RedInvoiceNewDataDTO;

import java.util.Date;
import java.util.List;

public interface RedInvoiceService extends BaseService {
    CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice> getAll(Long shopId, String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable);
    CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> getDataInBillOfSale(List<String> orderCodeList, Long shopId);
    List<ProductDetailDTO> getAllProductByOrderNumber(String orderCode);
    String create(RedInvoiceNewDataDTO redInvoiceNewDataDTO, Long userId, Long shopId);
    List<RedInvoicePrint> lstRedInvoicePrint(List<Long> ids);
    String deleteByIds(List<Long> ids);
}
