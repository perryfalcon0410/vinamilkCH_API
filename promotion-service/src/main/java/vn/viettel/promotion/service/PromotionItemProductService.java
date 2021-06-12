package vn.viettel.promotion.service;

import java.util.List;

public interface PromotionItemProductService {
    List<Long> listProductsNotAccumulated(List<Long> productIds);
}
