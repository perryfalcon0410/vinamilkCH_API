package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.repository.BaseRepository;

public interface CustomerRepository extends BaseRepository<Customer> {
    Customer findByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT COUNT(id) FROM customers", nativeQuery = true)
    int getCustomerNumber();
}
