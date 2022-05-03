package vn.viettel.sale.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleDay;

@Repository
public interface SaleDayRepository extends BaseRepository<SaleDay>, JpaSpecificationExecutor<SaleDay> {
	
	@Query(value = "select sd.dayNumber from SaleDay sd "
			+ "where (sd. month = :date or 1=1) and sd.shopId = :shopId ")
	public Integer getDayMonthByShopId(Long shopId, LocalDate date);
}
