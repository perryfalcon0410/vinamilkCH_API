package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoDetailRepository extends BaseRepository<PoDetail>, JpaSpecificationExecutor<PoDetail> {
    List<PoDetail> findByPoId(Long poId);
    @Query(value = "SELECT * FROM PO_DETAIL WHERE PRICE > 0 AND PO_ID =:id ", nativeQuery = true)
    List<PoDetail> getPoDetailByPoIdAndPriceIsGreaterThan(Long id);
    @Query(value = "SELECT * FROM PO_DETAIL WHERE PRICE = 0 AND PO_ID =:id ", nativeQuery = true)
    List<PoDetail> getPoDetailByPoIdAndPriceIsLessThan(Long id);
}
