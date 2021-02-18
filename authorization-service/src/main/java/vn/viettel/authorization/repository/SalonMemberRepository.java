package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.SalonMember;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface SalonMemberRepository extends BaseRepository<SalonMember> {
    Optional<List<SalonMember>> findAllByMemberIdAndDeletedAtIsNull(Long memberId);

    Optional<SalonMember> getByMemberIdAndSalonIdAndDeletedAtIsNull(Long memberId, Long salonId);
}
