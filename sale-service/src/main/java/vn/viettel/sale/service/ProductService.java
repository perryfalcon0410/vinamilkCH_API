package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.service.dto.OrderProductsDTO;
import vn.viettel.sale.service.dto.ProductDTO;

import java.util.List;

public interface ProductService extends BaseService {
    Response<Page<ProductInfo>> findAllProductInfo(Integer status, Integer type, Pageable pageable);

    Response<ProductDTO> getProduct(Long id, Long customerTypeId, Long shopId);

    Response<Page<ProductDTO>> findProducts(ProductFilter productFilter, Pageable pageable);

    Response<Page<ProductDTO>> findProductsTopSale(Long shopId, String keyWork, Long customerId, Pageable pageable);

    Response<OrderProductsDTO> changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> products);

}
