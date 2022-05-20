package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.PoAutoCoreShopProduct;

public interface PoAutoCoreShopProductRepository extends BaseRepository<PoAutoCoreShopProduct>, JpaSpecificationExecutor<PoAutoCoreShopProduct>{

	@Query(value = "select poc from PoAutoCoreShopProduct poc "
			+ "where poc.shopId = :shopId and poc.productId = :productId")
	public PoAutoCoreShopProduct getByShopIdAndProductId(Long shopId, Long productId);
}
