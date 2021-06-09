package vn.viettel.promotion.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.VoucherSaleProduct;

import java.util.List;

public interface VoucherSaleProductRepository extends BaseRepository<VoucherSaleProduct> {
    List<VoucherSaleProduct> findByVoucherProgramIdAndStatus(Long programId, Integer status);
}
