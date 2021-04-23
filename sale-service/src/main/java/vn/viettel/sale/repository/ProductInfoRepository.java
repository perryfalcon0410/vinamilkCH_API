package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.sale.entities.ProductInfo;
import vn.viettel.core.repository.BaseRepository;

public interface ProductInfoRepository extends BaseRepository<ProductInfo>, JpaSpecificationExecutor<ProductInfo> {
    ProductInfo findByIdAndType(Long id, Integer type);
}
