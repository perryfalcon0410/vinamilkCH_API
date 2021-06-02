package vn.viettel.promotion.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.PromotionCustATTRDetail;
import vn.viettel.promotion.repository.PromotionCustATTRDetailRepository;
import vn.viettel.promotion.service.PromotionCustAttrService;
import java.util.List;

@Service
public class CustomerPromotionServiceImpl extends BaseServiceImpl<PromotionCustATTRDetail,PromotionCustATTRDetailRepository> implements PromotionCustAttrService {

    @Override
    public List<Long> getListCusCard(Long programId, Long objectType) {
        List<Long> cusCards = repository.getPromotionCustATTRDetail(programId, objectType);
        if(cusCards == null) throw new ValidateException(ResponseMessage.NO_CUS_CARD_IS_APPLIED_PROMOTION);
        return cusCards;
    }
}
