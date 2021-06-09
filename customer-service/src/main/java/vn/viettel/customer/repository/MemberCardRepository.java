package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.MemberCard;

import java.util.List;
import java.util.Optional;

public interface MemberCardRepository extends BaseRepository<MemberCard> {
    Optional<MemberCard> getMemberCardByMemberCardCode(String memberCardCode);
    Optional<MemberCard> getMemberCardById(Long id);
    Optional<List<MemberCard>> getAllByCustomerTypeId(Long id);

    @Query(value = "SELECT * FROM MEMBER_CARD m " +
            " JOIN MEMBER_CUSTOMER mc ON m.id = mc.MEMBER_CARD_ID " +
            " WHERE mc.CUSTOMER_ID =:customerId AND m.STATUS = 1 ", nativeQuery = true)
    Optional<MemberCard> getByCustomerId(Long customerId);

}
