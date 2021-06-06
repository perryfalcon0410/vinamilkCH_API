package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.OrderProductRequest;
import vn.viettel.sale.messaging.ProductFilter;
import vn.viettel.sale.service.dto.*;

import java.util.List;

public interface ProductService extends BaseService {
    Page<ProductInfoDTO> findAllProductInfo(Integer status, Integer type, Pageable pageable);

    OrderProductDTO getProduct(Long id, Long customerTypeId, Long shopId);

    Page<OrderProductDTO> findProducts(ProductFilter productFilter, Pageable pageable);

    Page<OrderProductDTO> findProductsTopSale(Long shopId, String keyWork, Long customerTypeId, Integer checkStocktotal, Pageable pageable);

    Page<OrderProductDTO> findProductsMonth(Long shopId, Long customerTypeId, Pageable pageable);

    Page<OrderProductDTO> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable);

    OrderProductsDTO changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> products);

    Response<List<OrderProductDTO>> findProductsByKeyWord(String keyWord);

    List<ProductDataSearchDTO> findAllProduct(String keyWord);

    Response<Page<ProductDTO>> findProduct(String productCodes, String productName, Long catId,Pageable pageable);

    Response<List<ProductInfoDTO>> getAllProductCat();

    List<FreeProductDTO> getFreeProductDTONoOrder(Long shopId, Long warehouseId, String keyWord, int page);
}
