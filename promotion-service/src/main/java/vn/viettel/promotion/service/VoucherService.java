package vn.viettel.promotion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.db.entity.voucher.VoucherSaleProduct;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.promotion.messaging.VoucherUpdateRequest;
import vn.viettel.promotion.service.dto.VoucherDTO;

import java.util.List;

public interface VoucherService extends BaseService {
    // find voucher for sale
    Response<Page<VoucherDTO>> findVouchers(String keyWord, Long shopId, Long customerTypeId, Pageable pageable);

    Response<Voucher> getVoucher(Long id);

    Response<VoucherDTO> updateVoucher(Long id, VoucherUpdateRequest request, String username);

    Response<List<VoucherSaleProduct>> findVoucherSaleProducts(Long voucherProgramId);

    Response<List<Voucher>> getVoucherBySaleOrderId(long id);
}
