package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.Product;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ProductRepository extends BaseRepository<Product> {
    List<Product> findByProTypeId(long id);

    @Query(value = "SELECT * FROM product WHERE name like %:name%", nativeQuery = true)
    List<Product> findByProductName(String name);

    List<Product> findByProductCode(String code);

    @Query(value = "SELECT * FROM product WHERE istop = 1", nativeQuery = true)
    List<Product> findTopProduct();

}
