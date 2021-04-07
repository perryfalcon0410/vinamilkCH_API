package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.messaging.ProductInfoFilter;
import vn.viettel.sale.service.dto.ProductDTO;

public interface ProductService extends BaseService {
    Response<Page<ProductInfo>> findAllProductInfo(ProductInfoFilter filter, Pageable pageable);

    Response<Page<ProductDTO>> findProductByProductInfo(ProductInfoFilter filter, Pageable pageable);

    Response<ProductDTO> getProduct(Long id, Long customerTypeId);

    Response<Page<ProductDTO>> findProductsByNameOrCode(ProductFilter filter, Pageable pageable);

    Response<Page<ProductDTO>> findProductsTopSale(ProductFilter filter, Pageable pageable);
}
