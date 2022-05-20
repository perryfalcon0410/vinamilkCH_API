package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.ShopProduct;

public interface ShopProductRepository extends BaseRepository<ShopProduct>{
	
	public ShopProduct findByShopIdAndProductIdAndType(Long shopId, Long productId, int type);
	
	public ShopProduct findByShopIdAndCatIdAndType(Long shopId, Long catId, int type);
}
