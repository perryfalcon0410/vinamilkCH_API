package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.VoucherCustomerMap;

import java.util.List;

public interface VoucherCustomerMapRepository extends BaseRepository<VoucherCustomerMap>, JpaSpecificationExecutor<VoucherCustomerMap> {

    @Query("Select customerTypeId From VoucherCustomerMap Where voucherProgramId =:programId And status =:status")
    List<Long> findCustomerIds(Long programId, Integer status);
}
