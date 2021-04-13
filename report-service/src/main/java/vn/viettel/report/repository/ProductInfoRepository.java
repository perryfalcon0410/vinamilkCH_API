package vn.viettel.report.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.repository.BaseRepository;

public interface ProductInfoRepository extends BaseRepository<ProductInfo>, JpaSpecificationExecutor<ProductInfo> {
    ProductInfo findByIdAndType(Long id, Integer type);
}
