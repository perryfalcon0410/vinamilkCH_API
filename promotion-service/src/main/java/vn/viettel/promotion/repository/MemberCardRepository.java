//package vn.viettel.promotion.repository;
//
//import org.springframework.stereotype.Repository;
//import vn.viettel.core.db.entity.voucher.MemberCard;
//import vn.viettel.core.repository.BaseRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface MemberCardRepository extends BaseRepository<MemberCard> {
//    Optional<MemberCard> getMemberCardByMemberCardCodeAndDeletedAtIsNull(String memberCardCode);
//    Optional<MemberCard> getMemberCardByIdAndDeletedAtIsNull(Long id);
//    Optional<List<MemberCard>> getAllByCustomerTypeId(Long id);
//}
