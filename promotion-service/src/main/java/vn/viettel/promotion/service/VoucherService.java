package vn.viettel.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.db.entity.voucher.VoucherSaleProduct;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.messaging.VoucherFilter;
import vn.viettel.promotion.messaging.VoucherUpdateRequest;
import vn.viettel.promotion.service.dto.VoucherDTO;

import java.util.List;

public interface VoucherService extends BaseService {
    // find voucher for sale
    Response<Page<VoucherDTO>> findVouchers(VoucherFilter voucherFilter, Pageable pageable);

    Response<VoucherDTO> getVoucher(Long id, Long shopId, Long customerTypeId);

    Response<Voucher> getFeignVoucher(Long id);

    Response<VoucherDTO> updateVoucher(Long id, VoucherUpdateRequest request, Long userId);

    Response<List<VoucherSaleProduct>> findVoucherSaleProducts(Long voucherProgramId);

    Response<List<Voucher>> getVoucherBySaleOrderId(long id);
}
