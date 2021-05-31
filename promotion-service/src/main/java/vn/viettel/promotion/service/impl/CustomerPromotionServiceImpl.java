package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.PromotionCustATTRDetail;
import vn.viettel.promotion.repository.PromotionCusAttrRepository;
import vn.viettel.promotion.repository.PromotionCustATTRDetailRepository;
import vn.viettel.promotion.service.CustomerPromotionService;

import java.util.List;

@Service
public class CustomerPromotionServiceImpl extends BaseServiceImpl<PromotionCustATTRDetail,PromotionCustATTRDetailRepository> implements CustomerPromotionService {

    @Override
    public List<Long> getListCusType(Long promotionId) {
        List<Long> cusTypeIds = repository.getCusTypeIdByProgramId(promotionId);
        if(cusTypeIds == null) throw  new ValidateException(ResponseMessage.NO_CUSTOMER_TYPE_IS_APPLIED_PROMOTION);
        return cusTypeIds;
    }
}
