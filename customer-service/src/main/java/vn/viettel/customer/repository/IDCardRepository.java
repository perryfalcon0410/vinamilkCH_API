package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.IDCard;
import vn.viettel.core.repository.BaseRepository;

public interface IDCardRepository extends BaseRepository<IDCard> {
    @Query(value = "SELECT * FROM ID_CARDS WHERE ID_NO = :idNumber ", nativeQuery = true)
    IDCard findByIdNumber(String idNumber);
}
