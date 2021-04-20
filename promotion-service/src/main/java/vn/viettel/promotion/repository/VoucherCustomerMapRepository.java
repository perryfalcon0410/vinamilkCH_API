package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.voucher.VoucherCustomerMap;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface VoucherCustomerMapRepository extends BaseRepository<VoucherCustomerMap>, JpaSpecificationExecutor<VoucherCustomerMap> {

    @Query(value = "SELECT * FROM VOUCHER_CUSTOMER_MAP WHERE VOUCHER_PROGRAM_ID =:programId AND CUSTOMER_TYPE_ID =:customerTypeId " +
            "AND STATUS = 1 AND DELETED_AT IS NULL", nativeQuery = true)
    Optional<VoucherCustomerMap> checkVoucherCustomerMap(Long programId, Long customerTypeId);
}
