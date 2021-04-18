package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {
    Product getProductByProductCode(String productCode);
    Optional<Product> getProductByProductCodeAndDeletedAtIsNull(String productCode);
    @Query(value = "SELECT PRODUCT_CODE FROM PRODUCTS ", nativeQuery = true)
    List<String> getProductCode();
}
