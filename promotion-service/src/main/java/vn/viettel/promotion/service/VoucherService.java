package vn.viettel.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.service.dto.VoucherDTO;

public interface VoucherService extends BaseService {
    // find voucher for sale
    Response<Page<VoucherDTO>> findVouchers(String keyWord, Long shopId, Long customerTypeId, Pageable pageable);
}
