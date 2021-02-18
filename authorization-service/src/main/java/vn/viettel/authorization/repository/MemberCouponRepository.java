package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.MemberCoupon;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends BaseRepository<MemberCoupon> {
    Optional<List<MemberCoupon>> findAllByMemberIdAndDeletedAtIsNull(Long memberId);

    MemberCoupon findByCouponIdAndMemberIdAndDeletedAtIsNull(Long couponId, Long memberId);
}
