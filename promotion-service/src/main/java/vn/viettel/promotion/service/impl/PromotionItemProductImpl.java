package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.promotion.repository.PromotionItemProductRepository;
import vn.viettel.promotion.service.PromotionItemProductService;

import java.util.List;

@Service
public class PromotionItemProductImpl implements PromotionItemProductService {
    @Autowired
    PromotionItemProductRepository itemProductService;

    public List<Long> listProductsNotAccumulated(List<Long> productIds) {
        List<Long> ProductsNotAccumulated = itemProductService.productsNotAccumulated(productIds);
        return ProductsNotAccumulated;
    }
}
