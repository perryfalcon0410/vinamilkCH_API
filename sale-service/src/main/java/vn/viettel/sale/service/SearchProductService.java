package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.messaging.Response;

public interface SearchProductService {
    Response<Page<ProductType>> getAllProductType(Pageable pageable);
    Response<Page<Product>> getProductByProductTypeId(long proTypeId, Pageable pageable);
    Response<Page<Product>> getProductByNameOrCode(String input, Pageable pageable);
    Response<Page<Product>> getTopProduct(Pageable pageable);
}
