package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.PoDetail;
import vn.viettel.core.db.entity.stock.StockBorrowing;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoDetailRepository extends BaseRepository<PoDetail> {
    List<PoDetail> findByPoId(Long poId);
    @Query(value = "SELECT * FROM PO_DETAIL  WHERE PRICE is not null ", nativeQuery = true)
    List<PoDetail> getPoDetailByPoIdAndPriceIsNotNull(Long id);

    @Query(value = "SELECT * FROM PO_DETAIL  WHERE PRICE is null ", nativeQuery = true)
    List<PoDetail> getPoDetailByTrans(Long id);
}
