package vn.viettel.saleservice.service;

import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.db.entity.Shop;
import vn.viettel.core.db.entity.User;
import vn.viettel.core.db.entity.WareHouse;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.ReceiptCreateRequest;
import vn.viettel.saleservice.service.dto.ReceiptImportDTO;
import vn.viettel.saleservice.service.dto.ReceiptSearch;
import vn.viettel.saleservice.service.dto.WareHouseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptImportService {
    Response<List<ReceiptImportDTO>> getAll(ReceiptSearch receiptSearch);
    Response<ReceiptImport> createReceiptImport(ReceiptCreateRequest reccr, long userId,long idShop);
    Response<ReceiptImport> updateReceiptImport(ReceiptCreateRequest reccr, long userId);
    void remove(long[] ids);
    User checkUserExist(long userId);
    String createReceiptImportCode(Long idShop);
    Response<ReceiptImportDTO> getReceiptImportById(Long receiID);
}
