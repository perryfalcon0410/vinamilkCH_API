package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.ProductDetailDTO;
import vn.viettel.sale.service.dto.RedInvoiceDTO;
import vn.viettel.sale.service.dto.RedInvoiceDataDTO;

import java.util.Date;
import java.util.List;

public interface RedInvoiceService extends BaseService {
    Response<Page<RedInvoiceDTO>> getAll(String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable);
    Response<List<RedInvoiceDataDTO>> getDataInBillOfSale(List<String> orderCodeList, Long shopId);
    Response<List<ProductDetailDTO>> getAllProductByOrderNumber(String orderCode);
    Response<Object> create(RedInvoiceDataDTO redInvoiceDataDTO, Long userId, Long shopId);
}
