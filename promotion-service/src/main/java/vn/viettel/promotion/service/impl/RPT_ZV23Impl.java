package vn.viettel.promotion.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.promotion.entities.PromotionProgramDetail;
import vn.viettel.promotion.entities.PromotionProgramProduct;
import vn.viettel.promotion.entities.RPT_ZV23;
import vn.viettel.promotion.repository.PromotionProgramDetailRepository;
import vn.viettel.promotion.repository.PromotionProgramProductRepository;
import vn.viettel.promotion.repository.PromotionRPT_ZV23Repository;
import vn.viettel.promotion.service.RPT_ZV23Service;
import vn.viettel.promotion.service.dto.PriceDTO;
import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.promotion.service.dto.TotalPriceZV23DTO;
import vn.viettel.promotion.service.feign.SaleClient;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class RPT_ZV23Impl implements RPT_ZV23Service {
    @Autowired
    PromotionRPT_ZV23Repository rpt_zv23Repository;
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    PromotionProgramProductRepository promotionProgramProduct;
    @Autowired
    PromotionProgramDetailRepository detailRepository;
    @Autowired
    SaleClient saleClient;

    public RPT_ZV23DTO checkSaleOrderZV23(Long promotionId, Long customerId, Long shopId) {
        List<RPT_ZV23> rpt_zv23s = rpt_zv23Repository.checkZV23Require(promotionId, customerId, shopId, new Date());
        RPT_ZV23 rpt_zv23 = new RPT_ZV23();
        if(rpt_zv23s.size() == 0) return null;
        else {
            if(rpt_zv23s.size() == 1)
                rpt_zv23 = rpt_zv23s.get(0);
            else {
                RPT_ZV23 first = rpt_zv23s.get(0);
                RPT_ZV23 second = rpt_zv23s.get(1);
                double diff = first.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - second.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                if(diff > 0) rpt_zv23 = first;
                else rpt_zv23 = second;
            }
        }
        List<PromotionProgramDetail> details = detailRepository.findByPromotionProgramId(rpt_zv23.getPromotionProgramId());
        RPT_ZV23DTO dto = new RPT_ZV23DTO();
        for (PromotionProgramDetail detail:details) {
                if(rpt_zv23.getTotalAmount() < detail.getSaleAmt()) {
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    dto = modelMapper.map(rpt_zv23, RPT_ZV23DTO.class);
                }else return null;
            }
        return dto;
    }

    public TotalPriceZV23DTO VATorNotZV23(Long promotionId, Integer quantity) {
        double TotalAmountNotVAT = 0, TotalAmount = 0F;
        TotalPriceZV23DTO total = new TotalPriceZV23DTO();
        List<PromotionProgramProduct> applicableProductsNoTVAT = promotionProgramProduct.findApplicableProductsNotVAT(promotionId); // sp áp dụng
        if(applicableProductsNoTVAT.size() != 0) {
            for(PromotionProgramProduct pr:applicableProductsNoTVAT) {
                PriceDTO priceDTO = saleClient.getPriceByPrIdV1(pr.getProductId()).getData();
                TotalAmountNotVAT = TotalAmountNotVAT + priceDTO.getPriceNotVat()*quantity;
            }
            total.setTotalPriceNotVAT(TotalAmountNotVAT);
            total.setTotalPrice(0);
        }else {
            List<PromotionProgramProduct> applicableProducts = promotionProgramProduct.findApplicableProducts(promotionId); // sp áp dụng
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

    @Override
    public Boolean updateRPT_ZV23(Long id, RPT_ZV23Request request) {
        RPT_ZV23 zv23 = rpt_zv23Repository.getRPT_ZV23(id).orElseThrow(() -> new ValidateException(ResponseMessage.RPT_ZV23_NOT_EXISTS));
        zv23.setTotalAmount(request.getTotalAmount());
        rpt_zv23Repository.save(zv23);
        return true;
    }
}

