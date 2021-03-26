package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {
//    Page<Product> findByProductTypeId(long id, Pageable pageable);

    @Query(value = "SELECT * FROM PRODUCTS WHERE PRODUCT_NAME like %:name%", nativeQuery = true)
    Page<Product> findByProductName(String name, Pageable pageable);

    Product findByProductCode(String code);

    @Query(value = "SELECT * FROM PRODUCTS WHERE IS_TOP = 1", nativeQuery = true)
    Page<Product> findTopProduct(Pageable pageable);
}
