package vn.viettel.promotion.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.PromotionCustATTRDetail;
import vn.viettel.promotion.repository.PromotionCustATTRDetailRepository;
import vn.viettel.promotion.service.PromotionCustAttrService;
import java.util.Set;

@Service
public class CustomerPromotionServiceImpl extends BaseServiceImpl<PromotionCustATTRDetail,PromotionCustATTRDetailRepository> implements PromotionCustAttrService {

    @Override
    public Set<Long> getListCusCard(Long programId, Integer objectType) {
        Set<Long> cusCards = repository.getPromotionCustATTRDetail(programId, objectType);
        return cusCards;
    }
}
