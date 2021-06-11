package vn.viettel.promotion.service;

import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface VoucherService extends BaseService {
    // find voucher for sale

    VoucherDTO getVoucherByCode(String serial, Long shopId, Long customerId, List<Long> productIds);

    VoucherDTO getFeignVoucher(Long id);

    VoucherDTO updateVoucher(VoucherDTO voucherDTO);

    List<VoucherSaleProductDTO> findVoucherSaleProducts(Long voucherProgramId);

    List<VoucherDTO> getVoucherBySaleOrderId(long id);
}
