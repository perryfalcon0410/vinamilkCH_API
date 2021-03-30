package vn.viettel.promotion.repository;

import vn.viettel.core.db.entity.voucher.VoucherSaleProduct;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface VoucherSaleProductRepository extends BaseRepository<VoucherSaleProduct> {
    List<VoucherSaleProduct> findVoucherSaleProductByVoucherProgramIdAndStatus(Long programId, Integer status);
}
