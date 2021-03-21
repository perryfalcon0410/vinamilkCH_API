package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> getCustomerByCustomerCode(String cusCode);

    Customer getCustomerById(Long id);

    List<Customer> getCustomersByShopId(Long shopId);
}
