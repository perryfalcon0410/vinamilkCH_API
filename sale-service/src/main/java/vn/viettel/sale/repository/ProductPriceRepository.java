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
    List<Price> findProductPriceWithType(List<Long> productIds, Long customerTypeId, LocalDateTime date);

    @Query(value = "select id , product_id,PRICE_TYPE, price ,PRICE_NOT_VAT,vat,from_date,to_date,status,created_at,updated_at,created_by,updated_by, customer_type_id\n" +
            "        from ( " +
            "        select id , product_id,PRICE_TYPE, price ,PRICE_NOT_VAT,vat,from_date,to_date,status,created_at,updated_at,created_by,updated_by, customer_type_id  ,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY from_date desc , customer_type_id asc ) rn \n" +
            "        from prices\n" +
            "        where 1=1 and price_type = -1 " +
            "        and from_date <=:date " +
            "        and customer_type_id in (select id from customer.customer_type " +
            "                                   where 1=1 " +
            "                                   and status = 1 " +
            "                                   and warehouse_type_id =:wareHouseTypeId ) " +
            "        and status = 1 " +
            "        order by product_id , customer_type_id ) " +
            "        where rn = 1 and product_id in (:productIds) ", nativeQuery = true)
    List<Price> findProductPriceWithType1(List<Long> productIds, Long wareHouseTypeId, LocalDateTime date);

    @Query(value = "SELECT p FROM Price p " +
            "WHERE p.status = :status AND (COALESCE(:productIds, NULL) IS NULL OR p.productId IN (:productIds)) AND p.priceType = 1 " +
            " AND (:date IS NULL OR (p.fromDate <= :date AND p.toDate IS NULL) ) " +
            " order by p.fromDate desc, p.id "
    )
    List<Price> findProductPrice(List<Long> productIds, Integer status, LocalDateTime date);
}
