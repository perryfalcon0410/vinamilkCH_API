package vn.viettel.saleservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.ReceiptExport;
import vn.viettel.core.db.entity.ReceiptExportAdjusted;
import vn.viettel.core.db.entity.ReceiptExportBorrow;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.*;

import java.util.List;

public interface ReceiptExportService {
    Response<Page<ReceiptExportDTO>> getReceiptExportBySearch(ReceiptSearch receiptSearch, Pageable pageable);
    Response<ReceiptExport> createReceiptExport(ReceiptExportRequest rexr, long userId, long idShop);
    String createReceiptExportCode(Long idShop);
    String createInvoiceAdjustedCode(Long idShop);
    Response<ReceiptExportDTO> getReceiptExportById(Long recxId);
    Response<Page<ReceiptExportAdjustedDTO>> getAllReceiptExportAdjusted(Pageable pageable);
    Response<List<ReceiptExportAdjustedDetailDTO>> getExportAdjustedDetailById( Long Id);
    Response<Page<ReceiptExportBorrowDTO>> getAllReceiptExportBorrow(Pageable pageable);
    Response<List<ReceiptExportBorrowDetailDTO>> getExportBorrowDetailById( Long Id);
    Response<ReceiptExport> updateReceiptExport(ReceiptExportRequest rexr, long userId);
    void remove(long[] ids);
}
