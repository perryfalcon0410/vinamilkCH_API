package vn.viettel.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.messaging.VoucherFilter;
import vn.viettel.promotion.messaging.VoucherUpdateRequest;

import java.util.List;

public interface VoucherService extends BaseService {
    // find voucher for sale
    Response<Page<VoucherDTO>> findVouchers(VoucherFilter voucherFilter, Pageable pageable);

    Response<VoucherDTO> getVoucher(Long id, Long shopId, Long customerTypeId);

    Response<Voucher> getFeignVoucher(Long id);

    Response<VoucherDTO> updateVoucher(Long id, VoucherUpdateRequest request, Long userId);

    Response<List<VoucherSaleProductDTO>> findVoucherSaleProducts(Long voucherProgramId);

    Response<List<VoucherDTO>> getVoucherBySaleOrderId(long id);
}
