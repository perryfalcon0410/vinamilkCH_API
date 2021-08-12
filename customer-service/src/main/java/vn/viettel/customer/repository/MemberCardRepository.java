package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.dto.customer.CustomerMemberCardDTO;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.customer.entities.MemberCard;

import java.util.List;
import java.util.Optional;

public interface MemberCardRepository extends BaseRepository<MemberCard> {

    Optional<MemberCard> getMemberCardByMemberCardCode(String memberCardCode);

    Optional<MemberCard> getMemberCardById(Long id);

    Optional<List<MemberCard>> getAllByCustomerTypeId(Long id);

    @Query(value = "SELECT m FROM MemberCard m " +
            " JOIN MemberCustomer mc ON m.id = mc.memberCardId " +
            " WHERE mc.customerId =:customerId AND m.status = 1 ")
    Optional<MemberCard> getByCustomerId(Long customerId);

    @Query(value = "SELECT m FROM MemberCard m " +
            " JOIN MemberCustomer mc ON m.id = mc.memberCardId " +
            " WHERE mc.customerId IN (:customerIds) AND m.status = 1 ")
    List<MemberCard> getByCustomerIds(List<Long> customerIds);

    @Query(value = "SELECT DISTINCT NEW vn.viettel.core.dto.customer.CustomerMemberCardDTO(m.memberCardCode, m.memberCardName, mc.customerId) FROM MemberCard m " +
            " JOIN MemberCustomer mc ON m.id = mc.memberCardId " +
            " WHERE mc.customerId IN (:customerIds) AND m.status = 1 ")
    List<CustomerMemberCardDTO> getCustomerMemberCard(List<Long> customerIds);
}
