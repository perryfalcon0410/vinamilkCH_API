package vn.viettel.customer.repository;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerTypeRepository extends BaseRepository<CustomerType> {
    Optional<CustomerType> findById(Long id);
}
