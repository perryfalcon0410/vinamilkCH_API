package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseService;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.dto.ProductDetailDTO;
import vn.viettel.sale.service.dto.RedInvoiceDTO;
import vn.viettel.sale.service.dto.RedInvoiceDataDTO;
import vn.viettel.sale.service.dto.RedInvoiceNewDataDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface RedInvoiceService extends BaseService {
    CoverResponse<Page<RedInvoiceDTO>, TotalRedInvoice> getAll(Long shopId, String searchKeywords, Date fromDate, Date toDate, String invoiceNumber, Pageable pageable);
    CoverResponse<List<RedInvoiceDataDTO>, TotalRedInvoiceResponse> getDataInBillOfSale(List<String> orderCodeList, Long shopId);
    List<ProductDetailDTO> getAllProductByOrderNumber(String orderCode);
    RedInvoiceDTO create(RedInvoiceNewDataDTO redInvoiceNewDataDTO, Long userId, Long shopId);
    ResponseMessage deleteByIds(List<Long> ids);
    ByteArrayInputStream exportExcel(String ids, Integer type) throws IOException;
    ResponseMessage updateRed(List<RedInvoiceRequest> redInvoiceRequests, Long userId);
    CoverResponse<List<ProductDataResponse>, PrintDataRedInvoiceResponse> getDataPrint(Long id, Long shopId);
}
