package vn.viettel.promotion.service;

import vn.viettel.core.dto.promotion.RPT_ZV23DTO;
import vn.viettel.core.messaging.RPT_ZV23Request;

import java.util.List;
import java.util.Set;

public interface RPT_ZV23Service {
    RPT_ZV23DTO checkSaleOrderZV23(String promotionCode, Long customerId, Long shopId);
    Boolean updateRPT_ZV23(Long id, RPT_ZV23Request request);
    Boolean createRPT_ZV23(RPT_ZV23Request request);
    List<RPT_ZV23DTO> findByProgramIds(Set<Long> programIds, Long customerId, Long shopId);

}
