package vn.viettel.saleservice.service;

import vn.viettel.core.db.entity.ProductType;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface SearchProductService {
    Response<List<ProductType>> getAllProductType();
    Response<List<Product>> getProductByProductTypeId(long proTypeId);
    Response<List<Product>> getProductByNameOrCode(String input);
    Response<List<Product>> getTopProduct();
}
