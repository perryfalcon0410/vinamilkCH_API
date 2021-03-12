package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product> {
    List<Product> findByProductTypeId(long id);

    @Query(value = "SELECT * FROM PRODUCTS WHERE PRODUCT_NAME like %:name%", nativeQuery = true)
    List<Product> findByProductName(String name);

    Product findByProductCode(String code);

    @Query(value = "SELECT * FROM PRODUCTS WHERE IS_TOP = 1", nativeQuery = true)
    List<Product> findTopProduct();
}