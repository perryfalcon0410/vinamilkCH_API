package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.VoucherShopMap;

import java.util.List;
import java.util.Optional;

public interface VoucherShopMapRepostiory extends BaseRepository<VoucherShopMap>, JpaSpecificationExecutor<VoucherShopMap> {

    @Query(value = "SELECT * FROM VOUCHER_SHOP_MAP WHERE VOUCHER_PROGRAM_ID =:programId AND SHOP_ID =:shopId " +
            "AND STATUS = 1 ", nativeQuery = true)
    Optional<VoucherShopMap> checkVoucherShopMap(Long programId, Long shopId);

    @Query("Select shopId From VoucherShopMap Where voucherProgramId =:programId And status =:status")
    List<Long> findShopIds(Long programId, Integer status);

}
