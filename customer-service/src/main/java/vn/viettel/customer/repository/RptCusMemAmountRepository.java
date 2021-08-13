package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.RptCusMemAmount;

import java.util.List;
import java.util.Optional;

public interface RptCusMemAmountRepository extends BaseRepository<RptCusMemAmount> {
    Optional<RptCusMemAmount> findByCustomerIdAndStatus(Long Id, Integer status);
}
