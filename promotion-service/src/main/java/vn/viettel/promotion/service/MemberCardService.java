package vn.viettel.promotion.service;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.service.dto.MemberCardDTO;

import java.util.Optional;

@Service
public interface MemberCardService extends BaseService {
    Optional<MemberCard> getMemberCardById(Long id);
    Response<MemberCard> create(MemberCardDTO memberCardDTO,Long userId);
    Optional<MemberCard> getMemberCardByMemberCardCode(String code);
    Response<MemberCard> update(MemberCardDTO memberCardDTO);
}
