package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.Price;
import vn.viettel.core.repository.BaseRepository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductPriceRepository extends BaseRepository<Price> {

    @Query(value = "SELECT p FROM Price p " +
            " WHERE p.status = 1 AND p.productId IN (:productIds) AND p.priceType = -1 AND (:customerTypeId is null OR p.customerTypeId =:customerTypeId ) " +
            " AND (:date IS NULL OR (p.fromDate <= :date AND p.toDate IS NULL) ) " +
            " order by p.fromDate desc, p.customerTypeId"
    )
    List<Price> findProductPrice(List<Long> productIds, Long customerTypeId, LocalDateTime date);

    @Query(value = "SELECT p FROM Price p " +
            "WHERE p.status = :status AND (COALESCE(:productIds, NULL) IS NULL OR p.productId IN (:productIds)) AND p.priceType = 1 " +
            " AND (:date IS NULL OR (p.fromDate <= :date AND p.toDate IS NULL) ) " +
            " order by p.fromDate desc, p.id "
    )
    List<Price> findProductPrice(List<Long> productIds, Integer status, LocalDateTime date);
}
