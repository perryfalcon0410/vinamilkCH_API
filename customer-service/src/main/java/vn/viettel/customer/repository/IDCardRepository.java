package vn.viettel.customer.repository;

import vn.viettel.core.db.entity.IDCard;
import vn.viettel.core.repository.BaseRepository;

public interface IDCardRepository extends BaseRepository<IDCard> {
    IDCard findByIdNumber(String idNumber);
}
