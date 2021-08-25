package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.MemberCustomer;

import java.util.List;
import java.util.Optional;

public interface MemBerCustomerRepository extends BaseRepository<MemberCustomer> {

    Optional<MemberCustomer> findByCustomerId(long id);

    @Query("SELECT mem FROM MemberCustomer mem Join MemberCard card On mem.memberCardId = card.id " +
            " Where mem.customerId = :customerId And card.status = 1")
    Optional<MemberCustomer> getMemberCustomer(Long customerId);

    @Query("SELECT mem FROM MemberCustomer mem Join MemberCard card On mem.memberCardId = card.id " +
            " Where mem.customerId In :customerIds And card.status = 1")
    List<MemberCustomer> getMemberCustomers(List<Long> customerIds);

    @Query("SELECT mem.scoreCumulated FROM MemberCustomer mem Join MemberCard card On mem.memberCardId = card.id " +
            " Where mem.customerId = :customerId And card.status = 1")
    Double getScoreCumulated(Long customerId);
}
