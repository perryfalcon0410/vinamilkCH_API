package vn.viettel.customer.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.RptCusMemAmount;

import java.util.Optional;

public interface RptCusMemAmountRepository extends BaseRepository<RptCusMemAmount> {
    Optional<RptCusMemAmount> findByCustomerIdAndStatus(Long Id, Integer status);
}
