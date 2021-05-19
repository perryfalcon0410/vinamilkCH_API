package vn.viettel.customer.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.MemberCard;

import java.util.List;
import java.util.Optional;

public interface MemberCardRepository extends BaseRepository<MemberCard> {
    Optional<MemberCard> getMemberCardByMemberCardCode(String memberCardCode);
    Optional<MemberCard> getMemberCardById(Long id);
    Optional<List<MemberCard>> getAllByCustomerTypeId(Long id);
}
