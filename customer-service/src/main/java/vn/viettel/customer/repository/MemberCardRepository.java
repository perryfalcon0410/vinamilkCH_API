package vn.viettel.customer.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.MemberCard;

import java.util.List;
import java.util.Optional;

public interface MemberCardRepository extends BaseRepository<MemberCard> {
    Optional<MemberCard> getMemberCardByMemberCardCodeAndDeletedAtIsNull(String memberCardCode);
    Optional<MemberCard> getMemberCardByIdAndDeletedAtIsNull(Long id);
    Optional<List<MemberCard>> getAllByCustomerTypeId(Long id);
}
