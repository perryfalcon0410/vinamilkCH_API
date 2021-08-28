package vn.viettel.promotion.service;

import java.util.Set;

public interface PromotionCustAttrService {
    Set<Long> getListCusCard(Long programId, Integer objectType);
}
