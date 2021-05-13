package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.messaging.ProductRequest;
import vn.viettel.sale.service.dto.*;

import java.util.List;

public interface ProductService extends BaseService {
    Response<Page<ProductInfoDTO>> findAllProductInfo(Integer status, Integer type, Pageable pageable);

    Response<OrderProductDTO> getProduct(Long id, Long customerTypeId, Long shopId);

    Response<Page<OrderProductDTO>> findProducts(ProductFilter productFilter, Pageable pageable);

    Response<Page<OrderProductDTO>> findProductsTopSale(Long shopId, String keyWork, Long customerTypeId, Pageable pageable);

    Response<Page<OrderProductDTO>> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable);

    Response<OrderProductsDTO> changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> products);

    Response<List<OrderProductDTO>> findProductsByKeyWord(String keyWord);

    Response<List<ProductDataSearchDTO>> findAllProduct(ProductRequest request);

    Response<Page<ProductDTO>> findProduct(String productCodes, String productName, Long catId,Pageable pageable);

    Response<List<ProductInfoDTO>> getAllProductCat();
}
