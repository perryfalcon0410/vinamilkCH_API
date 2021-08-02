package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.VoucherShopMap;

import java.util.List;

public interface VoucherShopMapRepostiory extends BaseRepository<VoucherShopMap>, JpaSpecificationExecutor<VoucherShopMap> {

    @Query("Select shopId From VoucherShopMap Where voucherProgramId =:programId And status =:status")
    List<Long> findShopIds(Long programId, Integer status);

}
