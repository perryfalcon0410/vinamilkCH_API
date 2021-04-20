package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoDetailRepository extends BaseRepository<PoDetail>, JpaSpecificationExecutor<PoDetail> {
    List<PoDetail> findByPoId(Long poId);
    List<PoDetail> getPoDetailByPoIdAndPriceIsNotNull(Long id);
    List<PoDetail> getPoDetailByPoIdAndPriceIsNull(Long id);
}
