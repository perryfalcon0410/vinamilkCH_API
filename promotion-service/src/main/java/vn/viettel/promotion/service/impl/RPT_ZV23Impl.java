package vn.viettel.promotion.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.promotion.entities.RPT_ZV23;
import vn.viettel.promotion.repository.PromotionProgramDetailRepository;
import vn.viettel.promotion.repository.PromotionProgramProductRepository;
import vn.viettel.promotion.repository.PromotionRPT_ZV23Repository;
import vn.viettel.promotion.service.RPT_ZV23Service;
import vn.viettel.promotion.service.feign.SaleClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public RPT_ZV23DTO checkSaleOrderZV23(String promotionCode, Long customerId, Long shopId) {
        RPT_ZV23 rpt_zv23 = rpt_zv23Repository.checkZV23Require(promotionCode, customerId, shopId);
        return modelMapper.map(rpt_zv23, RPT_ZV23DTO.class);
    }

    @Override
    public Boolean updateRPT_ZV23(Long id, RPT_ZV23Request request) {
        RPT_ZV23 zv23 = rpt_zv23Repository.findById(id).get();
        if(zv23 == null) return false;
        zv23.setTotalAmount(request.getTotalAmount());
        rpt_zv23Repository.save(zv23);
        return true;
    }

    @Override
    public Boolean createRPT_ZV23(RPT_ZV23Request request) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RPT_ZV23 rpt = modelMapper.map(request, RPT_ZV23.class);
        rpt_zv23Repository.save(rpt);
        return true;
    }

    @Override
    public List<RPT_ZV23DTO> findByProgramIds(Set<Long> programIds, Long customerId, Long shopId) {
        List<RPT_ZV23> rpts = rpt_zv23Repository.getByProgramIds(programIds, customerId, shopId);
        if(rpts == null || rpts.isEmpty()) return new ArrayList<>();
        List<RPT_ZV23DTO> dtos = rpts.stream().map(r -> modelMapper.map(r, RPT_ZV23DTO.class)).collect(Collectors.toList());
        return dtos;
    }

}

