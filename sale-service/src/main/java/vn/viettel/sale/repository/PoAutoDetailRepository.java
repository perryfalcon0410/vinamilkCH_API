package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.PoAutoDetail;

@Repository
public interface PoAutoDetailRepository extends BaseRepository<PoAutoDetail>, JpaSpecificationExecutor<PoAutoDetail> {
	
}
