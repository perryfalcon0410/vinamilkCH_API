package vn.viettel.sale.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SalePlan;

@Repository
public interface SalePlanRepository extends BaseRepository<SalePlan>, JpaSpecificationExecutor<SalePlan> {

	@Query(value = "select sp.quantity from SalePlan sp "
			+ "where sp.productId = :productId and sp.shopId = :shopId "
			+ "and (month = :date or 1=1)")
	public Long getQuantityByShopProduct(Long shopId, Long productId, LocalDate date);
	
}
