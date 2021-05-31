package vn.viettel.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.messaging.VoucherFilter;

import java.util.List;

public interface VoucherService extends BaseService {
    // find voucher for sale
    Page<VoucherDTO> findVouchers(VoucherFilter voucherFilter, Pageable pageable);

    VoucherDTO getVoucher(Long id, Long shopId, Long customerId, List<Long> productIds);

    Voucher getFeignVoucher(Long id);

    VoucherDTO updateVoucher(VoucherDTO voucherDTO);

    List<VoucherSaleProductDTO> findVoucherSaleProducts(Long voucherProgramId);

    List<VoucherDTO> getVoucherBySaleOrderId(long id);
}
