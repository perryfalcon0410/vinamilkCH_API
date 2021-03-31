package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.PoConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoConfirmRepository extends BaseRepository<PoConfirm> {
    @Query(value = "SELECT * FROM PO_CONFIRM  WHERE STATUS = 0 AND TYPE = 1 ", nativeQuery = true)
    List<PoConfirm> getPoConfirm();
}
