//package vn.viettel.promotion.service.impl;
//
//import org.modelmapper.convention.MatchingStrategies;
//import org.springframework.stereotype.Service;
//import vn.viettel.core.ResponseMessage;
//import vn.viettel.core.db.entity.voucher.MemberCard;
//import vn.viettel.core.exception.ValidateException;
//import vn.viettel.core.messaging.Response;
//import vn.viettel.core.service.BaseServiceImpl;
//import vn.viettel.promotion.repository.MemberCardRepository;
//import vn.viettel.promotion.service.MemberCardService;
//import vn.viettel.promotion.service.dto.MemberCardDTO;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class MemberCardServiceImpl extends BaseServiceImpl<MemberCard, MemberCardRepository> implements MemberCardService {
//
//    @Override
//    public Response<MemberCard> getMemberCardById(Long Id) {
//        Optional<MemberCard> memberCard = repository.getMemberCardByIdAndDeletedAtIsNull(Id);
//        if(!memberCard.isPresent())
//        {
//            throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
//        }
//        return new Response<MemberCard>().withData(memberCard.get());
//    }
//
//    @Override
//    public Response<MemberCard> create(MemberCardDTO memberCardDTO, Long userId) {
//        if(memberCardDTO.getMemberCardCode()!=null)
//        {
//            Optional<MemberCard> memberCard = repository.getMemberCardByMemberCardCodeAndDeletedAtIsNull(memberCardDTO.getMemberCardCode());
//            if (memberCard.isPresent()) {
//                throw new ValidateException(ResponseMessage.MEMBER_CARD_CODE_HAVE_EXISTED);
//            }
//        }
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        MemberCard memberCardRecord = modelMapper.map(memberCardDTO, MemberCard.class);
//        memberCardRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//
//        repository.save(memberCardRecord);
//        return new Response<MemberCard>().withData(memberCardRecord);
//    }
//
//    @Override
//    public Response<MemberCard> getMemberCardByMemberCardCode(String code) {
//        return new Response<MemberCard>().withData(repository.getMemberCardByMemberCardCodeAndDeletedAtIsNull(code).get());
//    }
//
//    @Override
//    public Response<MemberCard> update(MemberCardDTO memberCardDTO) {
//        Optional<MemberCard> memberOld = repository.getMemberCardByIdAndDeletedAtIsNull(memberCardDTO.getId());
//        if (memberOld == null) {
//            throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
//        }
//
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        MemberCard memberCardRecord = modelMapper.map(memberCardDTO, MemberCard.class);
//        memberCardRecord.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        repository.save(memberCardRecord);
//
//        return new Response().withData(memberCardRecord);
//    }
//
//    @Override
//    public Response<List<MemberCard>> getMemberCardByCustomerId(Long id) {
//        Optional<List<MemberCard>> memberCards = repository.getAllByCustomerTypeId(id);
//        if(!memberCards.isPresent())
//        {
//            throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
//        }
//        return new Response<List<MemberCard>>().withData(memberCards.get());
//    }
//
//    @Override
//    public Response<MemberCard> getMemberCardByMemberCardId(long id) {
//        Optional<MemberCard> memberCard = repository.findById(id);
//        if(!memberCard.isPresent())
//        {
//            throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
//        }
//        return new Response<MemberCard>().withData(memberCard.get());
//    }
//
//}
