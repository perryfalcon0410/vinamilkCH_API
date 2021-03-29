package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.ProductDTO;

public interface ProductService extends BaseService {
    Response<Page<ProductInfo>> findAllProductInfo(Integer status, Integer type, Pageable pageable);
    Response<Page<ProductDTO>> findProductByProductInfo(Long productInfoId, Long customerTypeId, Integer status, Pageable pageable);
    Response<Page<ProductDTO>> findProductsByNameOrCode(String keyWord, Long customerTypeId, Integer status, Pageable pageable);
    Response<Page<ProductDTO>> findProductsTopSale(Long shopId, Long customerId, Pageable pageable);
}
