package vn.viettel.sale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.PalletShopProduct;

@Repository
public interface PalletShopProductRepository extends BaseRepository<PalletShopProduct>, JpaSpecificationExecutor<PalletShopProduct> {

	@Query(value = "select psd from PalletShopProduct psd "
			+ "where psd.shopId = :shopId and psd.productId = :productId")
	public PalletShopProduct getByShopIdAndProductId(Long shopId, Long productId);
}
