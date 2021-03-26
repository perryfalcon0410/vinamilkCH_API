package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.PoDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PoDetailRepository extends BaseRepository<PoDetail> {
    List<PoDetail> findByPoId(Long poId);
}
