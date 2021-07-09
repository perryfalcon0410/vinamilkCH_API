package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.PoDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoDetailRepository extends BaseRepository<PoDetail>, JpaSpecificationExecutor<PoDetail> {

    List<PoDetail> findByPoId(Long poId);

    @Query(value = "SELECT pd FROM PoDetail pd WHERE pd.price > 0 AND pd.poId =:id ")
    List<PoDetail> getPoDetailByPoIdAndPriceIsGreaterThan(Long id);

    @Query(value = "SELECT pd FROM PoDetail pd WHERE (pd.price = 0 or pd.price = NULL) AND pd.poId =:id ")
    List<PoDetail> getPoDetailByPoIdAndPriceIsLessThan(Long id);
}
