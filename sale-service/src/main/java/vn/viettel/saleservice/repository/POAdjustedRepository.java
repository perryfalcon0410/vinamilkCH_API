package vn.viettel.saleservice.repository;

import vn.viettel.core.db.entity.POAdjusted;
import vn.viettel.core.db.entity.POConfirm;
import vn.viettel.core.repository.BaseRepository;

public interface POAdjustedRepository extends BaseRepository<POAdjusted> {
    POAdjusted findPOAdjustedByPoLicenseNumber(String poNo);
}
