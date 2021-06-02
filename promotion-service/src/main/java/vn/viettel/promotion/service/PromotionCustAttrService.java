package vn.viettel.promotion.service;

import java.util.List;

public interface PromotionCustAttrService {
    List<Long> getListCusCard(Long programId, Long objectType);
}
