package vn.viettel.saleservice.repository;

import vn.viettel.core.db.entity.POBorrow;
import vn.viettel.core.repository.BaseRepository;

public interface POBorrowRepository extends BaseRepository<POBorrow> {
    POBorrow findPOBorrowByPoBorrowNumber(String poNo);
}
