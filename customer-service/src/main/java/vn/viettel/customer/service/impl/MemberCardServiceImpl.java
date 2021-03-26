package vn.viettel.customer.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.messaging.MemberCardCreateRequest;
import vn.viettel.customer.repository.MemberCardRepository;
import vn.viettel.customer.service.MemberCardService;

import java.util.Optional;

@Service
public class MemberCardServiceImpl extends BaseServiceImpl<MemberCard, MemberCardRepository> implements MemberCardService {
    @Override
    public Response<MemberCard> create(MemberCardCreateRequest request, Long userId) {
        Optional<MemberCard> memberCard = repository.getMemberCardByMemberCardCode(request.getMemberCardCode());

        if (memberCard.isPresent()) {
            throw new ValidateException(ResponseMessage.MEMBER_CARD_CODE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberCard memberCardRecord = modelMapper.map(request, MemberCard.class);

        memberCardRecord = repository.save(memberCardRecord);

        return new Response<MemberCard>().withData(memberCardRecord);
    }
}
