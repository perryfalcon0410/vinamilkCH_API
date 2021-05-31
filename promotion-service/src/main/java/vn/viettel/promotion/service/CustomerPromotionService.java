package vn.viettel.promotion.service;

import vn.viettel.core.dto.customer.CustomerTypeDTO;

import java.util.List;

public interface CustomerPromotionService {
    List<Long> getListCusType(Long programId);
    List<Long> getListMemberCard(Long programId);
    List<Long> getListCusLoyal(Long programId);
    List<Long> getListCusCard(Long programId);
}
