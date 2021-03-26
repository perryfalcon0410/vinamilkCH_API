package vn.viettel.customer.repository;

import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface MemberCardRepository extends BaseRepository<MemberCard> {
    Optional<MemberCard> getMemberCardByMemberCardCode(String memberCardCode);
}
