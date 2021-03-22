package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.*;

import java.util.List;

public interface ReceiptImportService {
    Response<Page<ReceiptImportDTO>> getAll(Pageable pageable);
    Response<Page<ReceiptImportDTO>> getReceiptImportBySearch(String invoiceNumber, String fromDate,String toDate, Integer type, Pageable pageable);
    Response<Page<ReceiptImportDTO>> getAnyReceiptImportBySearch(ReceiptSearch receiptSearch, Pageable pageable);
    Response<ReceiptImport> createReceiptImport(POPromotionalRequest pro, long userId, long idShop);
    Response<ReceiptImport> updateReceiptImport(ReceiptCreateRequest reccr, long userId);
    void remove(long[] ids);
    User checkUserExist(long userId);
    String createReceiptImportCode(Long idShop);
    Response<ReceiptImportDTO> getReceiptImportById(Long receiID);
    PoPromotional createPoPromotional(PoPromotionalDTO poPro, long userId, String poNumer);
    List<PoPromotionalDetail> createPoPromotionalDetail(List<PoPromotionalDetailDTO> ppdds, long userId, long poId);
    Response<List<ReceiptImportDetailDTO>> getReceiptImportDetailByReciId(Long receiID);
}
