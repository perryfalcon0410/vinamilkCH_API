package vn.viettel.customer.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerMemberCardDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.customer.entities.MemberCard;
import vn.viettel.customer.repository.MemberCardRepository;
import vn.viettel.customer.service.MemberCardService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberCardServiceImpl extends BaseServiceImpl<MemberCard, MemberCardRepository> implements MemberCardService {

    @Override
    public MemberCardDTO getMemberCardById(Long Id) {
        Optional<MemberCard> memberCard = repository.getMemberCardById(Id);
        if(!memberCard.isPresent())
        {
            throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
        }
        return modelMapper.map(memberCard.get(),MemberCardDTO.class);
    }

    @Override
    public MemberCard create(MemberCardDTO memberCardDTO, Long userId) {
        if(memberCardDTO.getMemberCardCode()!=null)
        {
            Optional<MemberCard> memberCard = repository.getMemberCardByMemberCardCode(memberCardDTO.getMemberCardCode());
            if (memberCard.isPresent()) {
                throw new ValidateException(ResponseMessage.MEMBER_CARD_CODE_HAVE_EXISTED);
            }
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberCard memberCardRecord = modelMapper.map(memberCardDTO, MemberCard.class);

        repository.save(memberCardRecord);
        return memberCardRecord;
    }

    @Override
    public MemberCard update(MemberCardDTO memberCardDTO) {
        MemberCard memberOld = repository.getMemberCardById(memberCardDTO.getId()).orElse(null);
        if (memberOld == null) {
            throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberCard memberCardRecord = modelMapper.map(memberCardDTO, MemberCard.class);
        repository.save(memberCardRecord);

        return memberCardRecord;
    }

    @Override
    public List<MemberCardDTO> getMemberCardByCustomerId(Long id) {
        Optional<List<MemberCard>> memberCards = repository.getAllByCustomerTypeId(id);
        if(!memberCards.isPresent())
        {
            return new ArrayList<>();
        }
        List<MemberCardDTO> memberCardDTOS = memberCards.get().stream()
                .map(memberCard -> modelMapper.map(memberCard, MemberCardDTO.class))
                .collect(Collectors.toList());

        return memberCardDTOS;
    }

    @Override
    public MemberCardDTO getByCustomerId(Long customerId) {
        MemberCard memberCard = repository.getByCustomerId(customerId).orElse(null);
        if (memberCard == null) return null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberCardDTO dto = modelMapper.map(memberCard, MemberCardDTO.class);
        return dto;
    }

    @Override
    public List<MemberCardDTO> getByCustomerIds(List<Long> customerIds){
        List<MemberCard> memberCards = repository.getByCustomerIds(customerIds);
        if(memberCards == null) return null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return memberCards.stream().map(item -> modelMapper.map(item, MemberCardDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<CustomerMemberCardDTO> getCustomerMemberCard(List<Long> customerIds){
        return repository.getCustomerMemberCard(customerIds);
    }
}
