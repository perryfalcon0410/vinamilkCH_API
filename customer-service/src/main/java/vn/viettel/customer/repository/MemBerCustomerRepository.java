package vn.viettel.customer.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.MemberCustomer;

import java.util.Optional;

public interface MemBerCustomerRepository extends BaseRepository<MemberCustomer> {

    Optional<MemberCustomer> findByCustomerId(long id);
}
