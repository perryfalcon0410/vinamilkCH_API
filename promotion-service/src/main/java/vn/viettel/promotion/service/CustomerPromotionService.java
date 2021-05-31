package vn.viettel.promotion.service;

import vn.viettel.core.dto.customer.CustomerTypeDTO;

import java.util.List;

public interface CustomerPromotionService {
    List<Long> getListCusType(Long promotionId);
}
