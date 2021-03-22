package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.POConfirm;
import vn.viettel.core.repository.BaseRepository;

public interface POConfirmRepository extends BaseRepository<POConfirm> {

    POConfirm findPOConfirmByPoNumber(String poNo);

}
