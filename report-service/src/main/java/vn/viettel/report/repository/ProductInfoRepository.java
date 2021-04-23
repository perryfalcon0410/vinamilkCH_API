//package vn.viettel.report.repository;
//
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//
//public interface ProductInfoRepository extends BaseRepository<ProductInfo>, JpaSpecificationExecutor<ProductInfo> {
//    ProductInfo findByIdAndType(Long id, Integer type);
//    @Query(value = "SELECT * FROM PRODUCT_INFO WHERE TYPE =1 ", nativeQuery = true)
//    List<ProductInfo> getAllProductInfo();
//}
