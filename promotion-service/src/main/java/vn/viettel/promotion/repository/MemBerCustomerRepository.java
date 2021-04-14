package vn.viettel.promotion.repository;

import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface MemBerCustomerRepository extends BaseRepository<MemberCustomer> {
    Optional<MemberCustomer> getMemberCustomerByCustomerId(Long id);

    Optional<MemberCustomer> findByCustomerId(long id);
}
