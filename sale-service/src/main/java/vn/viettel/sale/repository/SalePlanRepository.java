package vn.viettel.sale.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SalePlan;

@Repository
public interface SalePlanRepository extends BaseRepository<SalePlan>, JpaSpecificationExecutor<SalePlan> {

	@Query(value = "select sp.quantity from SalePlan sp "
			+ "where sp.productId = :productId and sp.shopId = :shopId "
			+ "and month = :date")
	public Long getQuantityByShopProduct(Long shopId, Long productId, Date date);
	
}
