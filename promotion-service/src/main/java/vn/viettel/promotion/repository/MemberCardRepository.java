package vn.viettel.promotion.repository;

import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface MemberCardRepository extends BaseRepository<MemberCard> {
    Optional<MemberCard> getMemberCardByMemberCardCodeAndDeletedAtIsNull(String memberCardCode);
    Optional<MemberCard> getMemberCardByIdAndDeletedAtIsNull(Long id);
}
