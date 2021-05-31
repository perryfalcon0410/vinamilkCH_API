package vn.viettel.promotion.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.PromotionCustATTRDetail;
import vn.viettel.promotion.repository.PromotionCustATTRDetailRepository;
import vn.viettel.promotion.service.CustomerPromotionService;
import java.util.List;

@Service
public class CustomerPromotionServiceImpl extends BaseServiceImpl<PromotionCustATTRDetail,PromotionCustATTRDetailRepository> implements CustomerPromotionService {

    @Override
    public List<Long> getListCusType(Long programId) {
        List<Long> cusTypeIds = repository.getCusTypeIdByProgramId(programId);
        if(cusTypeIds == null) throw  new ValidateException(ResponseMessage.NO_CUSTOMER_TYPE_IS_APPLIED_PROMOTION);
        return cusTypeIds;
    }

    @Override
    public List<Long> getListMemberCard(Long programId) {
        List<Long> memberCards = repository.getMemberCardIdByProgramId(programId);
        if(memberCards == null) throw  new ValidateException(ResponseMessage.NO_MEMBER_CARD_IS_APPLIED_PROMOTION);
        return memberCards;
    }

    @Override
    public List<Long> getListCusLoyal(Long programId) {
        List<Long> cusLoyals = repository.getCusLoyalByProgramId(programId);
        if(cusLoyals == null) throw  new ValidateException(ResponseMessage.NO_MEMBER_CARD_IS_APPLIED_PROMOTION);
        return cusLoyals;
    }

    @Override
    public List<Long> getListCusCard(Long programId) {
        List<Long> cusCards = repository.getCusCardByProgramId(programId);
        if(cusCards == null) throw new ValidateException(ResponseMessage.NO_CUS_CARD_IS_APPLIED_PROMOTION);
        return cusCards;
    }
}
