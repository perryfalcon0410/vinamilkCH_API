package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.common.ProductInfo;
import vn.viettel.core.repository.BaseRepository;

public interface ProductInfoRepository extends BaseRepository<ProductInfo>, JpaSpecificationExecutor<ProductInfo> {

}
