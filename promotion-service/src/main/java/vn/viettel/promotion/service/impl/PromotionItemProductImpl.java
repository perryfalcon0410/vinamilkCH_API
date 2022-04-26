package vn.viettel.promotion.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.promotion.entities.PromotionItemProduct;
import vn.viettel.promotion.entities.PromotionProgram;
import vn.viettel.promotion.repository.PromotionItemProductRepository;
import vn.viettel.promotion.repository.PromotionProgramRepository;
import vn.viettel.promotion.service.PromotionItemProductService;

import java.util.List;

@Service
public class PromotionItemProductImpl extends BaseServiceImpl<PromotionItemProduct, PromotionItemProductRepository> implements PromotionItemProductService {
    @Autowired
    PromotionItemProductRepository itemProductServiceIml;

    public List<Long> listProductsNotAccumulated(List<Long> productIds) {
        List<Long> ProductsNotAccumulated = itemProductServiceIml.productsNotAccumulated(productIds);
        return ProductsNotAccumulated;
    }
}
