package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.MOQShopProduct;

public interface MOQShopProductRepository extends BaseRepository<MOQShopProduct>, JpaSpecificationExecutor<MOQShopProduct> {
	
	@Query(value = "select moq from MOQShopProduct moq "
			+ "where moq.shopId = :shopId and moq.productId = :productId")
	MOQShopProduct getByShopIdAndProductId(Long shopId, Long productId);
}
