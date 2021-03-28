package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.common.Price;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface ProductPriceRepository extends BaseRepository<Price> {

    @Query(
        value = "SELECT * FROM PRICES WHERE PRODUCT_ID =:productId AND CUSTOMER_TYPE_ID =:customerTypeId " +
            "AND STATUS = 1 AND PRICE_TYPE = -1 "
    , nativeQuery = true)
    Price getProductPrice(Long productId, Long customerTypeId);
    Price findByProductId(Long id);
}
