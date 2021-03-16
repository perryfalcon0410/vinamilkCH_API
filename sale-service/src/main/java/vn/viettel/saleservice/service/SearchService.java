package vn.viettel.saleservice.service;

import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.ReceiptSearch;

import java.util.List;

public interface SearchService {
    // search product
    Response<List<ProductType>> getAllProductType();
    Response<List<Product>> getProductByProductTypeId(long proTypeId);
    Response<List<Product>> getProductByNameOrCode(String input);
    Response<List<Product>> getTopProduct();
    // search receipt online
    Response<List<ReceiptOnline>> searchReceiptOnline(ReceiptSearch searchInfo);
}
