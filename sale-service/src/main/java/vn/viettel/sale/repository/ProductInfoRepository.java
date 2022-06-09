package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.ProductInfo;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ProductInfoRepository extends BaseRepository<ProductInfo>, JpaSpecificationExecutor<ProductInfo> {

    @Query(value = "SELECT pi FROM ProductInfo pi WHERE pi.type =1 ")
    List<ProductInfo> getAllProductInfo();
    
    @Query(value = "select pi from ProductInfo pi where id = :catId")
    ProductInfo getProductInfoCodeById(Long catId);
}
