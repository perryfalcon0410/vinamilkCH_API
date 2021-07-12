package vn.viettel.promotion.service;

import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.promotion.service.dto.TotalPriceZV23DTO;

public interface RPT_ZV23Service {
    RPT_ZV23DTO checkSaleOrderZV23(String promotionCode, Long customerId, Long shopId);
    TotalPriceZV23DTO VATorNotZV23(Long promotionId, Integer quantity);
    Boolean updateRPT_ZV23(Long id, RPT_ZV23Request request);
    Boolean createRPT_ZV23(RPT_ZV23Request request);
}
