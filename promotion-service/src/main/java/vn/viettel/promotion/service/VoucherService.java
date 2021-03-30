package vn.viettel.promotion.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.service.dto.VoucherDTO;

import java.util.List;

public interface VoucherService extends BaseService {
    // find voucher for sale
    Response<List<VoucherDTO>> findVouchers(String keyWord, Long shopId, Long customerTypeId);
}
