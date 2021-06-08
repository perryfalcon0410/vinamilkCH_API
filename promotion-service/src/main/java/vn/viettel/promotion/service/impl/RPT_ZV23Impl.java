package vn.viettel.promotion.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.PromotionProgramDetail;
import vn.viettel.promotion.entities.PromotionProgramProduct;
import vn.viettel.promotion.entities.RPT_ZV23;
import vn.viettel.promotion.repository.PromotionProgramDetailRepository;
import vn.viettel.promotion.repository.PromotionProgramProductRepository;
import vn.viettel.promotion.repository.PromotionRPT_ZV23Repository;
import vn.viettel.promotion.service.RPT_ZV23Service;
import vn.viettel.promotion.service.dto.PriceDTO;
import vn.viettel.promotion.service.dto.RPT_ZV23DTO;
import vn.viettel.promotion.service.dto.TotalPriceZV23DTO;
import vn.viettel.promotion.service.feign.SaleClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RPT_ZV23Impl implements RPT_ZV23Service {
    @Autowired
    PromotionRPT_ZV23Repository rpt_zv23;
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    PromotionProgramProductRepository promotionProgramProduct;
    @Autowired
    PromotionProgramDetailRepository detailRepository;
    @Autowired
    SaleClient saleClient;

    public Boolean checkSaleOrderZV23(PromotionProgramDTO programDTO, Long customerId, Long shopId) {
        List<RPT_ZV23> rpt_zv23s = rpt_zv23.checkZV23Require(customerId, shopId, programDTO.getId());
        if(rpt_zv23s.size() == 0) throw new ValidateException(ResponseMessage.CUSTOMER_NOT_IN_RPT_ZV23);
        for(RPT_ZV23 rpt_zv23:rpt_zv23s){
            List<PromotionProgramDetail> details = detailRepository.findByPromotionProgramId(rpt_zv23.getPromotionProgramId());
            for (PromotionProgramDetail detail:details) {
                if(rpt_zv23.getTotalAmount() < detail.getSaleAmt());
                return true;
            }
        }
        return false;
    }

    public TotalPriceZV23DTO VATorNotZV23(PromotionProgramDTO programDTO, Integer quantity) {
        double TotalAmountNotVAT = 0, TotalAmount = 0F;
        TotalPriceZV23DTO total = new TotalPriceZV23DTO();
        List<PromotionProgramProduct> applicableProductsNoTVAT = promotionProgramProduct.findApplicableProductsNotVAT(programDTO.getId()); // sp áp dụng
        if(applicableProductsNoTVAT.size() != 0) {
            for(PromotionProgramProduct pr:applicableProductsNoTVAT) {
                PriceDTO priceDTO = saleClient.getPriceByPrIdV1(pr.getProductId()).getData();
                TotalAmountNotVAT = TotalAmountNotVAT + priceDTO.getPriceNotVat()*quantity;
            }
            total.setTotalPriceNotVAT(TotalAmountNotVAT);
            total.setTotalPrice(0);
        }else {
            List<PromotionProgramProduct> applicableProducts = promotionProgramProduct.findApplicableProducts(programDTO.getId()); // sp áp dụng
            if(applicableProducts.isEmpty()) throw new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
            for(PromotionProgramProduct pr:applicableProducts) {
                PriceDTO priceDTO = saleClient.getPriceByPrIdV1(pr.getProductId()).getData();
                TotalAmount = TotalAmount + priceDTO.getPriceNotVat()*quantity;
            }
            total.setTotalPriceNotVAT(0);
            total.setTotalPrice(TotalAmount);
        }
        return total;
    }
}

