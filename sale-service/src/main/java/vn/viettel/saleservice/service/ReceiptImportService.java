package vn.viettel.saleservice.service;

import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptImportService {
    Response<List<ReceiptImportDTO>> getAll(ReceiptSearch receiptSearch);
    Response<ReceiptImport> createReceiptImport(POPromotionalRequest pro, long userId,long idShop);
    Response<ReceiptImport> updateReceiptImport(ReceiptCreateRequest reccr, long userId);
    void remove(long[] ids);
    User checkUserExist(long userId);
    String createReceiptImportCode(Long idShop);
    Response<ReceiptImportDTO> getReceiptImportById(Long receiID);
    PoPromotional createPoPromotional(PoPromotionalDTO poPro, long userId,String poNumer);
    List<PoPromotionalDetail> createPoPromotionalDetail(List<PoPromotionalDetailDTO> ppdds, long userId, long poId);
}
