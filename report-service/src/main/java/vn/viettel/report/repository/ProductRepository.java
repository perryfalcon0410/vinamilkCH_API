//package vn.viettel.report.repository;
//
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Query;
//import vn.viettel.core.db.entity.common.Product;
//import vn.viettel.core.repository.BaseRepository;
//
//import java.util.List;
//
//public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {
//    @Query(value = "SELECT PRODUCT_CODE FROM PRODUCTS ", nativeQuery = true)
//    List<String> getProductCode();
//}
