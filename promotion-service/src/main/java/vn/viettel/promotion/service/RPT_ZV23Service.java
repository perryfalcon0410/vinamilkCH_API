package vn.viettel.promotion.service;

import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.promotion.service.dto.TotalPriceZV23DTO;

import java.time.LocalDateTime;

public interface RPT_ZV23Service {
    RPT_ZV23DTO checkSaleOrderZV23(Long promotionId, Long customerId, Long shopId, LocalDateTime useDate);
    TotalPriceZV23DTO VATorNotZV23(Long promotionId, Integer quantity);
}
