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

    Page<OrderProductDTO> findProducts(ProductFilter productFilter, Pageable pageable);

    Page<OrderProductDTO> findProductsTopSale(Long shopId, String keyWork, Long customerId, Integer checkStocktotal, Boolean barcode, Pageable pageable);

    Page<OrderProductDTO> findProductsMonth(Long shopId, Long customerId, Pageable pageable);

    Page<OrderProductDTO> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable);

    OrderProductsDTO changeCustomerType(Long customerTypeId, Long shopId, List<OrderProductRequest> products);

    List<OrderProductDTO> findProductsByKeyWord(Long shopId, Long customerId, String keyWord);

    List<ProductDataSearchDTO> findAllProduct(Long shopId, String keyWord);

    Page<ProductDTO> findProduct(Long shopId, String productCode, String productName, Long catId,Pageable pageable);

    Response<List<ProductInfoDTO>> getAllProductCat();

    List<FreeProductDTO> findFreeProductDTONoOrder(Long shopId, Long warehouseId, String keyWord, int page);

    PriceDTO getProductPriceById(Long shopId, Long productId);

    List<OrderProductDTO> getByBarcode(Long shopId, String barcode, Long customerId);
}
