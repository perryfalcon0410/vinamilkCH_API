package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.VoucherSaleProduct;

import java.util.List;

public interface VoucherSaleProductRepository extends BaseRepository<VoucherSaleProduct> {
    @Query("Select productId From VoucherSaleProduct Where voucherProgramId =:programId And status=:status ")
    List<Long> findProductIds(Long programId, Integer status);

    List<VoucherSaleProduct> findByVoucherProgramIdAndStatus(Long programId, Integer status);
}
