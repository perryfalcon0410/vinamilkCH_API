package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.RptCusMemAmount;

import java.util.List;
import java.util.Optional;

public interface RptCusMemAmountRepository extends BaseRepository<RptCusMemAmount> {
    Optional<RptCusMemAmount> findByCustomerIdAndStatus(Long Id, Integer status);
    Optional<RptCusMemAmount> findByCustomerIdAndCustShopIdAndStatus(Long customerId, Long shopId, Integer status);

    @Query("SELECT mem FROM RptCusMemAmount mem WHERE mem.status = 1 AND  mem.customerId IN (:customerIds)")
    List<RptCusMemAmount> findByCustomerIds(List<Long> customerIds);
}
