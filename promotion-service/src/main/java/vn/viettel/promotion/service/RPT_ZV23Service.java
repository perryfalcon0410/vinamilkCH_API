package vn.viettel.promotion.service;

import vn.viettel.core.dto.promotion.PromotionProgramDTO;
import vn.viettel.promotion.service.dto.RPT_ZV23DTO;
import vn.viettel.promotion.service.dto.TotalPriceZV23DTO;

import java.time.LocalDateTime;

public interface RPT_ZV23Service {
    Boolean checkSaleOrderZV23(PromotionProgramDTO programDTO, Long customerId, Long shopId);
    TotalPriceZV23DTO VATorNotZV23(PromotionProgramDTO programDTO, Integer quantity);
}
